package com.hybe.larva.service;

import com.hybe.larva.dto.banner.*;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.artist.ArtistRepoExt;
import com.hybe.larva.entity.banner.Banner;
import com.hybe.larva.entity.banner.BannerContents;
import com.hybe.larva.entity.banner.BannerRepoExt;
import com.hybe.larva.exception.ProtectedResourceException;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerService {

    private final BannerRepoExt bannerRepoExt;
    private final AlbumRepoExt albumRepoExt;
    private final ArtistRepoExt artistRepoExt;
    private final StorageService storageService;
    private final CacheUtil cacheUtil;

    public BannerDetailResponse addBanner(BannerAddRequest request) {

        try {
            int order = 0;
            if (Boolean.TRUE.equals(request.isDisplay())) {
                order = bannerRepoExt.getMaxSortOrder();
            }
            request.setSortOrder(order);

            Banner banner = Banner.builder()
                    .sortOrder(0)
                    .display(false)
                    .build();

            banner = bannerRepoExt.insert(banner);
            return new BannerDetailResponse(banner, cacheUtil);
        } catch (Exception e) {
            //파일 삭제
            final List<String> deleteTargets = request.getContents().values()
                    .stream()
                    .map(BannerContents::getImage)
                    .collect(Collectors.toList());
            storageService.deleteByKeys(deleteTargets);
            throw new ProtectedResourceException("배너 등록중 에러가 발생했습니다.");
        }
    }

    public Page<BannerSearchResponse> searchBanner(BannerSearchRequest request) {
        final Criteria criteria = Criteria.where(Banner.DELETED).ne(true);
        criteria.and(Banner.DISPLAY).ne(false);

        if (request.getArtistId() != null) {
            criteria.and(Banner.ARTIST_ID).is(request.getArtistId());
        }

        return bannerRepoExt.searchUser(criteria, request.getPageable())
                .map(banner -> new BannerSearchResponse(banner, cacheUtil));
    }

    public Page<BannerResponse> searchBannerForBackoffice(BannerSearchPageRequest request) {

        final Criteria criteria = Criteria.where(Banner.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Banner.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(Banner.TAGS).regex(request.getKeyword());
        }

        if (request.getAlbumId() != null) {
            criteria.and(Banner.ALBUM_ID).is(request.getAlbumId());
        }

        if (request.getArtistId() != null) {
            criteria.and(Banner.ARTIST_ID).is(request.getArtistId());
        }

        return bannerRepoExt.search(criteria, request.getPageable())
                .map(banner -> new BannerResponse(banner, cacheUtil));
    }

    public BannerSearchResponse getBanner(String bannerId) {
        Banner banner = bannerRepoExt.findById(bannerId);
        return new BannerSearchResponse(banner, cacheUtil);
    }

    public BannerDetailResponse getBannerForBackoffice(String bannerId) {
        Banner banner = bannerRepoExt.findById(bannerId);
        return new BannerDetailResponse(banner, cacheUtil);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public BannerDetailResponse updateBanner(String bannerId, BannerUpdateRequest request) {

        try {
            Banner banner = bannerRepoExt.findById(bannerId);

            // 정렬순서 계산
            int sortOrder = request.getSortOrder();
            final boolean originDisplayFlag = banner.isDisplay();
            if (Boolean.FALSE.equals(originDisplayFlag) && Boolean.TRUE.equals(request.isDisplay())) {
                sortOrder = bannerRepoExt.getMaxSortOrder();
            } else {
                if (Boolean.FALSE.equals(request.isDisplay())) {
                    sortOrder = 0;
                }
            }
            request.setSortOrder(sortOrder);

            final int originalSortOrder = banner.getSortOrder();

            // 삭제 대상 추출
            final List<String> deleteTargets = getDeleteTargets(banner, request);

            // 업데이트
            banner = banner.update(request, getAlbumTitle(request.getAlbumId()), getArtistName(request.getArtistId()));
            banner = bannerRepoExt.save(banner);

            // 기타 배너 재정렬
            if (Boolean.TRUE.equals(originDisplayFlag) && Boolean.FALSE.equals(request.isDisplay())) {
                bannerRepoExt.decSortOrder(originalSortOrder);
            }

            // 기존 파일 삭제
            storageService.deleteByKeys(deleteTargets);

            return new BannerDetailResponse(banner, cacheUtil);
        } catch (Exception e) {
            Banner banner = bannerRepoExt.findById(bannerId);
            final List<String> deleteTargets = getDeleteTargetsException(request, banner);
            // contents 가 없을 경우 삭제 처리
            if (banner.getContents() == null) {
                bannerRepoExt.delete(banner);
            }

//            //파일 삭제
//            // TODO : DB 내용과 비교해서 내용이 같다면 삭제하지 않는 로직?
//            final List<String> deleteTargets = request.getContents().values()
//                    .stream()
//                    .map(BannerContents::getImageKey)
//                    .collect(Collectors.toList());
            storageService.deleteByKeys(deleteTargets);
            throw new ProtectedResourceException("배너 갱신중 에러가 발생했습니다.");
        }
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteBanner(String bannerId) {
        Banner banner = bannerRepoExt.findById(bannerId);
//        deleteStorageList(banner);
        bannerRepoExt.decSortOrder(banner.getSortOrder());
        banner = banner.delete();
        bannerRepoExt.save(banner);
    }

//    private void deleteStorageList(Banner banner) {
//        List<String> deleteFileListKey = new ArrayList<>();
//
//        // thumbnail은 album이 지워질 때 삭제됨.
//
//        banner.getContents().forEach((key, val) -> {
//            deleteFileListKey.add(val.getImageKey());
//        });
//
//        deleteStorage(deleteFileListKey);
//    }
//
//    private void deleteStorage(List<String> deleteFileListKey) {
//        storageService.deleteExistingFile(deleteFileListKey);
//    }


    public void updateBannerSortOrder(BannerUpdateSortOrderRequest request) {
        for (BannerUpdateSortOrder bannerUpdateSortOrder : request.getOrderList()) {
            Banner banner = bannerRepoExt.findById(bannerUpdateSortOrder.getBannerId()).updateSortOrder(bannerUpdateSortOrder.getSortOrder());
            bannerRepoExt.save(banner);
        }
    }

    /**
     * DB와 리퀘스트의 카드 컨텐츠를 비교해서 삭제대상을 추출한다.
     * @param origin DB 카드 컨텐츠
     * @param req 리퀘스트 카드 컨텐츠
     * @return s3 삭제 대상 목록
     */
    private String getDeleteTargetThumbNail(String origin, String req) {

        if (null != origin && !origin.equals(req)) {
            return origin;
        }

        return null;
    }

    /**
     * DB와 리퀘스트의 컨텐츠를 비교해서 삭제대상을 추출한다.
     * @param banner 배너 엔티티 컨텐츠 맵 (Map<locale code, contents> origin)
     * @param request 배너 리퀘스트 컨텐츠 맵 (Map<locale code, contents> req)
     * @return s3 삭제 대상 목록
     */
    private List<String> getDeleteTargets(Banner banner, BannerUpdateRequest request) {
        List<String> deleteTargets = new ArrayList<>();

        deleteTargets.add(getDeleteTargetThumbNail(banner.getThumbnailKey(), request.getThumbnailKey()));

        Map<String, BannerContents> origin = banner.getContents();
        Map<String, BannerContents> req = request.getContents();

        Set<String> keys = req.keySet();

        if (origin != null) {
            for (final String key : keys) {

                if (!origin.get(key).getImage().equals(req.get(key).getImage())) {
                    deleteTargets.add(origin.get(key).getImage());
                }
            }
        }

        return deleteTargets;
    }

    /**
     * DB와 리퀘스트의 컨텐츠를 비교해서 삭제대상을 추출한다.
     * @param request 배너 엔티티 컨텐츠 맵 (Map<locale code, contents> origin)
     * @param banner 배너 리퀘스트 컨텐츠 맵 (Map<locale code, contents> req)
     * @return s3 삭제 대상 목록
     */
    private List<String> getDeleteTargetsException(BannerUpdateRequest request, Banner banner) {
        List<String> deleteTargets = new ArrayList<>();

        Map<String, BannerContents> origin = banner.getContents();
        Map<String, BannerContents> req = request.getContents();

        deleteTargets.add(getDeleteTargetThumbNail(request.getThumbnailKey(), banner.getThumbnailKey()));

        Set<String> keys = req.keySet();

        for (final String key : keys) {

            if (origin == null) {
                deleteTargets.add(req.get(key).getImage());
            } else {
                if (!req.get(key).getImage().equals(origin.get(key).getImage())) {
                    deleteTargets.add(req.get(key).getImage());
                }
            }
        }

        return deleteTargets;
    }

    private String getAlbumTitle(String albumId) {
        if (albumId == null || albumId.equals("") || albumId.trim().equals("")) {
            return albumId;
        }
        final Album album = albumRepoExt.findById(albumId);
        return album.getTitle();
    }

    private String getArtistName(String artistId) {
        if (artistId == null || artistId.equals("") || artistId.trim().equals("")) {
            return artistId;
        }
        final Artist artist = artistRepoExt.findById(artistId);
        return artist.getName();
    }
}

