package com.hybe.larva.service;

import com.hybe.larva.dto.album.*;
import com.hybe.larva.dto.banner.BannerUpdateRequest;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumAggregation;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.artist.ArtistRepoExt;
import com.hybe.larva.entity.banner.Banner;
import com.hybe.larva.entity.banner.BannerRepoExt;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.nfc_card.NfcCardRepoExt;
import com.hybe.larva.enums.OperationType;
import com.hybe.larva.exception.ProtectedResourceException;
import com.hybe.larva.exception.ResourceAlreadyInUseException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepoExt albumRepoExt;
    private final BannerRepoExt bannerRepoExt;
    private final NfcCardRepoExt nfcCardRepoExt;
    private final ArtistRepoExt artistRepoExt;
    private final StorageService storageService;
    private final BannerService bannerService;
    private final CacheUtil cacheUtil;

    public AlbumResponse addAlbum(AlbumAddRequest request) {

        try {
            Album album = request.toEntity();
            album = albumRepoExt.insert(album);
            return new AlbumResponse(album);
        } catch (Exception e) {
            //파일 삭제
            deleteRequestContents(request);
            throw new ProtectedResourceException("아티스트 등록중 에러가 발생했습니다.");
        }
    }

    public Page<AlbumResponse> searchAlbum(AlbumSearchRequest request) {
        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Album.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(Album.TAGS).regex(request.getKeyword());
        }

        if (request.getArtistId() != null) {
            criteria.and(Album.ARTIST_ID).is(request.getArtistId());
        }

        return albumRepoExt.search(criteria, request.getPageable())
                .map(album -> new AlbumResponse(album, cacheUtil));
    }

    public AlbumDetailResponse getAlbum(String albumId) {
        AlbumAggregation albumAggregation = albumRepoExt.findByIdDetail(albumId);
        return new AlbumDetailResponse(albumAggregation, cacheUtil);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public AlbumDetailResponse updateAlbum(String albumId, AlbumUpdateRequest request) {

        try {
            Album album = albumRepoExt.findById(albumId);
            album = album.update(request);
            album = albumRepoExt.save(album);

            return getAlbum(album.getId());
        } catch (Exception e) {
            //파일 삭제
//            storageService.delete(fileMap);
            throw new ProtectedResourceException("Artist failed to edit");
        }
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteAlbum(String albumId) {
//        Album album = albumRepoExt.findById(albumId);
//        if (album.getRefCount() > 0) {
//            throw new ProtectedResourceException("this accessory is in use: " + accessory);
//        }
//        albumRepoExt.delete(album);

        List<NfcCard> nfcCards = nfcCardRepoExt.findByAlbumId(albumId);

        if (nfcCards.size() > 0) {
            throw new ResourceAlreadyInUseException("Nfc Card", "앨범");
        }

        List<Banner> banners = bannerRepoExt.findByAlbumId(albumId);

        for (Banner banner : banners) {
            if (Boolean.TRUE.equals(banner.isDisplay())) {
                throw new ResourceAlreadyInUseException("배너", "앨범");
            }
            bannerService.deleteBanner(banner.getId());
        }

        Album album = albumRepoExt.findById(albumId);
        album = album.delete();
        albumRepoExt.save(album);

    }

    private void deleteStorage(List<String> deleteFileListKey) {
        storageService.deleteExistingFile(deleteFileListKey);
    }


    /**
     * 파일 삭제
     * @param req Add 리퀘스트
     */
    private void deleteRequestContents(AlbumAddRequest req) {
        final List<String> deleteTargets = req.getReward().getFiles();
        deleteTargets.add(req.getThumbnailKey());
        storageService.deleteByKeys(deleteTargets);
    }

    /**
     * DB와 리퀘스트의 카드 컨텐츠를 비교해서 삭제대상을 추출한다.
     * @param origin DB 카드 컨텐츠
     * @param req 리퀘스트 카드 컨텐츠
     * @return s3 삭제 대상 목록
     */
    private List<String> getDeleteTargets (CardContents origin, CardContents req) {
        List<String> deleteTargets = new ArrayList<>();
        if (null != origin) {
            if (null != origin.getThumbnailKey() && !origin.getThumbnailKey().equals(req.getThumbnailKey())) {
                deleteTargets.add(origin.getThumbnailKey());
            }
            if (null != origin.getImageKey() && !origin.getImageKey().equals(req.getImageKey())) {
                deleteTargets.add(origin.getImageKey());
            }
            if (null != origin.getVideoKey() && !origin.getVideoKey().equals(req.getVideoKey())) {
                deleteTargets.add(origin.getVideoKey());
            }
        }
        return deleteTargets;
    }

    /**
     * saveCard (등록/수정/삭제) 실패 시 파일 삭제
     * @param cardSaveRequests 삭제용 리퀘스트 컨텐츠
     */
    public void deleteCardSaveRequestContents(String id, List<CardSaveRequest> cardSaveRequests) {
        if (cardSaveRequests != null && cardSaveRequests.size() > 0) {
            List<String> deleteTargets = new ArrayList<>();
            Album album = albumRepoExt.findById(id);
            for (CardSaveRequest request : cardSaveRequests) {
                if (OperationType.INSERT.equals(request.getOperationType())) {
                    // 등록
                    deleteTargets.addAll(request.getContents().getFiles());
                } else if (OperationType.UPDATE.equals(request.getOperationType())) {
                    // 수정
                    if (album.getCards() != null && album.getCards().size() > 0) {
                        AlbumCard oldAlbumCard = findAlbumCard(album, request.getId());

                        if (oldAlbumCard != null) {
                            deleteTargets.addAll(getDeleteTargets(oldAlbumCard.getContents(), request.getContents()));
                        }
                    }
                }
            }
            storageService.deleteByKeys(deleteTargets);
        }
    }

    public void saveCard(String id, List<CardSaveRequest> cardSaveRequests) {
        if (cardSaveRequests != null && cardSaveRequests.size() > 0) {
            List<String> deleteTargets = new ArrayList<>();
            List<AlbumCard> albumCards = new ArrayList<>();
            Album album = albumRepoExt.findById(id);
            for (CardSaveRequest request : cardSaveRequests) {

                switch (request.getOperationType()) {
                    case DELETE :
                        try {
                            AlbumCard oldAlbumCard = findAlbumCard(album, request.getId());
                            if (oldAlbumCard != null) {
                                deleteTargets.addAll(oldAlbumCard.getContents().getFiles());
                            }

                            // 기존 파일 삭제는 나중에 한번에 처리
                        } catch (ResourceNotFoundException e2) {
                            throw new ResourceNotFoundException(e2.getMessage());
                        } catch (Exception e) {
                            throw new ProtectedResourceException("카드 삭제중 에러가 발생했습니다.");
                        }
                        break;
                    case UPDATE :
                        try {

                            if (album.getCards() != null && album.getCards().size() > 0) {
                                AlbumCard oldAlbumCard = findAlbumCard(album, request.getId());

                                if (oldAlbumCard != null) {
                                    deleteTargets.addAll(getDeleteTargets(oldAlbumCard.getContents(), request.getContents()));
                                }
                            }

                            albumCards.add(albumCardBuilder(request));

                            // 기존 파일 삭제는 나중에 한번에 처리
                        } catch (ResourceNotFoundException e2) {
                            throw new ResourceNotFoundException(e2.getMessage());
                        } catch (Exception e) {
                            log.info( "err : " + e.getMessage());
                            throw new ProtectedResourceException("카드 갱신중 에러가 발생했습니다.");
                        }
                        break;
                    case INSERT :
                    case NONE :
                        try {
                            albumCards.add(albumCardBuilder(request));
                        } catch (Exception e) {
                            throw new ProtectedResourceException("카드 등록중 에러가 발생했습니다.");
                        }
                        break;
                }
            }

            album.update(albumCards);
            albumRepoExt.save(album);
            // 파일 삭제
            deleteStorage(deleteTargets);
        }
    }

    private AlbumCard findAlbumCard(Album album, String cardId) {
        AlbumCard oldAlbumCard =
                album.getCards().stream()
                        .filter(ac -> ac.getId() != null)
                        .filter(ac -> ac.getId().equals(cardId))
                        .findFirst()
                        .orElse(null);
        return oldAlbumCard;
    }

    private AlbumCard albumCardBuilder(CardSaveRequest request) {
        AlbumCard albumCard = AlbumCard.builder()
                .id(request.getId())
                .position(request.getPosition())
                .tags(request.getTags())
                .members(request.getMembers())
                .contents(request.getContents())
                .build();
        return albumCard;
    }

    public List<CardResponse> getAlbumCard(String albumId) {
        Album album = albumRepoExt.findById(albumId);

        return Optional.ofNullable(album.getCards()).orElse(new ArrayList<>()).stream()
                .map(CardResponse::new)
                .sorted(Comparator.comparing(CardResponse::getPosition))
                .collect(Collectors.toList());
    }
}
