package com.hybe.larva.service;

import com.hybe.larva.config.job.AlbumJobConfiguration;
import com.hybe.larva.dto.album.AlbumSearchRequest;
import com.hybe.larva.dto.album.AlbumUpdateRequest;
import com.hybe.larva.dto.album.CardResponse;
import com.hybe.larva.dto.album.CardSaveRequest;
import com.hybe.larva.dto.album_working.*;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.album_working.*;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.nfc_card.NfcCardRepoExt;
import com.hybe.larva.enums.BatchState;
import com.hybe.larva.enums.AlbumState;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumWorkingService {

    private final AlbumWorkingRepoExt albumWorkingRepoExt;
    private final AlbumStateHistoryRepoExt albumStateHistoryRepoExt;
    private final NfcCardRepoExt nfcCardRepoExt;
    private final AlbumRepoExt albumRepoExt;
    private final AlbumJobConfiguration albumJobConfiguration;
    private final StorageService storageService;
    private final LanguagePackService languagePackService;
    private final CacheUtil cacheUtil;


    public AlbumWorkingResponse addAlbumWorking(AlbumWorkingAddRequest request) {

        try {
            AlbumWorking albumWorking = request.toEntity();
            albumWorking = albumWorkingRepoExt.insert(albumWorking);

            AlbumStateHistory albumStateHistory = AlbumStateHistory.builder()
                    .albumId(albumWorking.getId())
                    .history(Arrays.asList(StateHistory.builder()
                            .version(albumWorking.getVersion())
                            .state(AlbumState.WORKING)
                            .updatedAt(LocalDateTime.now())
                            .updatedBy(CommonUser.getUid())
                            .build()))
                    .build();

            albumStateHistoryRepoExt.insert(albumStateHistory);

            return new AlbumWorkingResponse(albumWorking);
        } catch (Exception e) {
            //파일 삭제
            deleteRequestContents(request);
            throw new ProtectedResourceException("아티스트 등록중 에러가 발생했습니다.");
        }
    }

    public Page<AlbumWorkingSearchResponse> searchAlbumWorking(AlbumSearchRequest request) {
        final Criteria criteria = Criteria.where(AlbumWorking.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(AlbumWorking.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(AlbumWorking.TAGS).regex(request.getKeyword());
        }

        if (request.getArtistId() != null) {
            criteria.and(AlbumWorking.ARTIST_ID).is(request.getArtistId());
        }

        if (request.getState() != null) {
            criteria.and(AlbumWorking.STATE).in(request.getState());
        }

        return albumWorkingRepoExt.search(criteria, request.getPageable())
                .map(album -> new AlbumWorkingSearchResponse(album, cacheUtil));
    }

    public AlbumWorkingDetailResponse getAlbumWorking(String albumId) {
        AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);
        StateHistory stateHistories = null;
        if (albumWorking.getState().equals(AlbumState.REJECTED)) {
            stateHistories = albumStateHistoryRepoExt.findHistoryVersionAndState(albumWorking.getId(), albumWorking.getVersion(), AlbumState.REJECTED);
        }
        return new AlbumWorkingDetailResponse(albumWorking, stateHistories, cacheUtil);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public AlbumWorkingDetailResponse updateAlbumWorking(String albumId, AlbumUpdateRequest request) {

        try {
            AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);

            // 파일 삭제 처리
//            List<String> deleteTargets = new ArrayList<>();            
//            deleteTargets.add(getDeleteTargetThumbNail(albumWorking.getThumbnailKey(), request.getThumbnailKey()));
//            deleteTargets.addAll(getDeleteTargets(albumWorking.getReward(), request.getReward()));

            albumWorking = albumWorking.update(request);
            albumWorking = albumWorkingRepoExt.save(albumWorking);


            return getAlbumWorking(albumWorking.getId());
        } catch (Exception e) {
            //파일 삭제
//            deleteAlbumWorkingUpdateRequestContents(albumId, request);
            throw new ProtectedResourceException("Artist failed to edit");
        }
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteAlbumWorking(String albumId) {
        Album album = albumRepoExt.findByExists(albumId);
        if (album != null) {
            throw new ResourceAlreadyInUseException("이미 배포된 앨범입니다.");
        }

        AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);
        albumWorking = albumWorking.delete();
        albumWorkingRepoExt.save(albumWorking);
    }

    /**
     * albumWorking-Cards 의 변경, 수정, 삭제된 contents 처리
      * @param deleteFileListKey
     */ 
    private void deleteStorage(List<String> deleteFileListKey) {
        storageService.albumWorkingContentsDeleteByKeys(deleteFileListKey);
    }

    /**
     * 상태 업데이트트
     * @param albumId     Album Warking Id 값
     * @param updateState update 될 state 값
     * @param compareState array 비교 해야 할 상태 값
     * @return
     */
    public AlbumWorkingDetailResponse updateAlbumWorkingState(String albumId, AlbumState updateState, List<AlbumState> compareState) {
        AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);

        if (compareState.contains(albumWorking.getState())) {

            if (updateState.equals(AlbumState.WORKING) &&
                (AlbumState.REJECTED.equals(albumWorking.getState()) || AlbumState.DEPLOYED.equals(albumWorking.getState()))) {
                albumWorking = albumWorkingRepoExt.incVersion(albumId);
            }

            albumWorking.setState(updateState);
            albumWorking = albumWorkingRepoExt.save(albumWorking);



            StateHistory stateHistory = StateHistory.builder()
                    .version(albumWorking.getVersion())
                    .state(albumWorking.getState())
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(CommonUser.getUid())
                    .build();
            albumStateHistoryRepoExt.pushHistory(albumId, stateHistory);
        }

        return getAlbumWorking(albumId);
    }


    /**
     * 상태 rejected 및 comment 추가
     * @param albumId
     * @param request
     * @return
     */
    public AlbumWorkingDetailResponse updateAlbumWorkingRejected(String albumId, AlbumWorkingRejectedRequest request) {
        AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);
        albumWorking.setState(AlbumState.REJECTED);
        albumWorking = albumWorkingRepoExt.save(albumWorking);

        StateHistory stateHistory = StateHistory.builder()
                .version(albumWorking.getVersion())
                .state(albumWorking.getState())
                .comment(request.getComment())
                .updatedAt(LocalDateTime.now())
                .updatedBy(CommonUser.getUid())
                .build();
        albumStateHistoryRepoExt.pushHistory(albumId, stateHistory);

        return getAlbumWorking(albumId);
    }


    /**
     * 파일 삭제
     * @param req Add 리퀘스트
     */
    private void deleteRequestContents(AlbumWorkingAddRequest req) {
        final List<String> deleteTargets = req.getReward().getFiles();
        deleteTargets.add(req.getThumbnailKey());
        storageService.albumWorkingContentsDeleteByKeys(deleteTargets);
    }

    /**
     * saveCard (등록/수정/삭제) 실패 시 파일 삭제
     * @param id 
     * @param request 리퀘스트 컨텐츠
     */
    public void deleteAlbumWorkingUpdateRequestContents(String id, AlbumUpdateRequest request) {

        List<String> deleteTargets = new ArrayList<>();
        AlbumWorking albumWorking = albumWorkingRepoExt.findById(id);

        if (albumWorking != null) {
            deleteTargets.add(getDeleteTargetThumbNail(albumWorking.getThumbnailKey(), request.getThumbnailKey()));
            deleteTargets.addAll(getDeleteTargets(albumWorking.getReward(), request.getReward()));
        }

        storageService.albumWorkingContentsDeleteByKeys(deleteTargets);
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
     * DB와 리퀘스트의 카드 컨텐츠를 비교해서 삭제대상을 추출한다.
     * @param origin DB 카드 컨텐츠
     * @param req 리퀘스트 카드 컨텐츠
     * @return s3 삭제 대상 목록
     */
    private List<String> getDeleteTargets(CardContents origin, CardContents req) {
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
            AlbumWorking albumWorking = albumWorkingRepoExt.findById(id);
            for (CardSaveRequest request : cardSaveRequests) {
                if (OperationType.INSERT.equals(request.getOperationType())) {
                    // 등록
                    deleteTargets.addAll(request.getContents().getFiles());
                } else if (OperationType.UPDATE.equals(request.getOperationType())) {
                    // 수정
                    if (albumWorking.getCards() != null && albumWorking.getCards().size() > 0) {
                        AlbumCard oldAlbumCard = findAlbumCard(albumWorking, request.getId());

                        if (oldAlbumCard != null) {
                            deleteTargets.addAll(getDeleteTargets(oldAlbumCard.getContents(), request.getContents()));
                        }
                    }
                }
            }
            storageService.albumWorkingContentsDeleteByKeys(deleteTargets);
        }
    }


    public void saveCard(String id, List<CardSaveRequest> cardSaveRequests) {
        if (cardSaveRequests != null && cardSaveRequests.size() > 0) {
            List<String> deleteTargets = new ArrayList<>();
            List<AlbumCard> albumCards = new ArrayList<>();
            AlbumWorking albumWorking = albumWorkingRepoExt.findById(id);
            for (CardSaveRequest request : cardSaveRequests) {

                switch (request.getOperationType()) {
                    case DELETE :
                        try {
                            AlbumCard oldAlbumCard = findAlbumCard(albumWorking, request.getId());
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

                            if (albumWorking.getCards() != null && albumWorking.getCards().size() > 0) {
                                AlbumCard oldAlbumCard = findAlbumCard(albumWorking, request.getId());

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

            albumWorking.update(albumCards);
            albumWorkingRepoExt.save(albumWorking);
            // 파일 삭제
            deleteStorage(deleteTargets);
        }
    }

    private AlbumCard findAlbumCard(AlbumWorking albumWorking, String cardId) {
        AlbumCard oldAlbumCard =
                albumWorking.getCards().stream()
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
        AlbumWorking albumWorking = albumWorkingRepoExt.findById(albumId);

        return Optional.ofNullable(albumWorking.getCards()).orElse(new ArrayList<>()).stream()
                .map(CardResponse::new)
                .sorted(Comparator.comparing(CardResponse::getPosition))
                .collect(Collectors.toList());
    }

    public Page<AlbumWorkingHistorySearchResponse> searchAlbumWorkingHistory(AlbumWorkingHistorySearchRequest request) {

        final Criteria defaultCriteria = Criteria.where(AlbumStateHistory.ALBUM_ID).is(request.getAlbumId());
        final Criteria criteria = new Criteria();

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(AlbumWorking.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getVersion() != null) {
            criteria.and(AlbumStateHistory.VERSION).is(request.getVersion());
        }

        if (request.getState() != null) {
            criteria.and(AlbumStateHistory.STATE).is(request.getState().name());
        }

        return albumStateHistoryRepoExt.searchHistory(defaultCriteria, criteria, request.getPageable())
                .map(AlbumWorkingHistorySearchResponse::new);
    }

    public void getAlbumStateBatchExecute() {

        try {
            albumJobConfiguration.transferAlbum();
        } catch(Exception e) {
            log.info("e : " + e.getMessage());
        }
    }

    public BatchJobEnabledResponse updateAlbumWorkingJobEnabled(BatchJobEnabledUpdateRequest request) {
        log.info(request.toString());
        languagePackService.setCacheJobEnabled(request.isJobEnabled());
        return getAlbumWorkingJobEnabled();
    }

    public BatchJobEnabledResponse getAlbumWorkingJobEnabled() {

        boolean isJobEnabled = cacheUtil.isJobEnabled();
        BatchState batchState = BatchState.OFF;
        if (Boolean.TRUE.equals(isJobEnabled)) {
            // 상태 조회 상태 값
            if (albumWorkingRepoExt.findDeployingCount()) {
                batchState = BatchState.WORKING;
            } else {
                batchState = BatchState.ON;
            }
        }

        return new BatchJobEnabledResponse(batchState);
    }

    public Page<AlbumWorkingBatchSearchResponse> searchAlbumWorkingBatch(AlbumSearchRequest request) {
        final Criteria criteria = Criteria.where(AlbumWorking.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(AlbumWorking.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(AlbumWorking.TAGS).regex(request.getKeyword());
        }

        if (request.getArtistId() != null) {
            criteria.and(AlbumWorking.ARTIST_ID).is(request.getArtistId());
        }

        if (request.getState() != null) {
            criteria.and(AlbumWorking.STATE).in(request.getState());
        }

        return albumWorkingRepoExt.search(criteria, request.getPageable())
                .map(album -> new AlbumWorkingBatchSearchResponse(album, cacheUtil));
    }
}
