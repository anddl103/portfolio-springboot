package com.hybe.larva.config.job;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.hybe.larva.config.AppInfo;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album.AlbumRepo;
import com.hybe.larva.entity.album_working.AlbumStateHistoryRepoExt;
import com.hybe.larva.entity.album_working.AlbumWorking;
import com.hybe.larva.entity.album_working.AlbumWorkingRepoExt;
import com.hybe.larva.entity.album_working.StateHistory;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AlbumJobConfiguration {

    private static final String JOB_NAME = "issue_album_job";
    private static final String STEP1_NAME = "issue_album_step1";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    private final AlbumStateHistoryRepoExt albumStateHistoryRepoExt;

    private final AlbumWorkingRepoExt albumWorkingRepoExt;

    private final AlbumRepo albumRepo;

    private final AmazonS3 amazonS3;

    private final MongoTransactionManager mongoTransactionManager;

    private final AppInfo appInfo;

    private final CacheUtil cacheUtil;

    @Scheduled(cron = "0 0 04 * * *", zone = "Asia/Seoul")
    public void transferAlbum() throws Exception {

        if (Boolean.TRUE.equals(cacheUtil.isJobEnabled())) {
            Step step1 = stepBuilderFactory.get(STEP1_NAME)
                    .tasklet(transferAlbumWorkingTasklet())
                    .transactionManager(mongoTransactionManager)
                    .build();
            Job job = jobBuilderFactory.get(JOB_NAME)
                    .incrementer(new RunIdIncrementer())
                    .start(step1)
                    .build();
            String jobId = JOB_NAME + "-" + System.currentTimeMillis();
            JobParameters params = new JobParametersBuilder()
                    .addString("jobId", jobId)
                    .toJobParameters();

            jobLauncher.run(job, params);
        }
    }

    @Bean
    public TransferAlbumWorkingTasklet transferAlbumWorkingTasklet() {
        return new TransferAlbumWorkingTasklet(appInfo,
                albumRepo,
                albumWorkingRepoExt,
                albumStateHistoryRepoExt,
                amazonS3,
                cacheUtil);
    }

    @RequiredArgsConstructor
    public class TransferAlbumWorkingTasklet implements Tasklet {

        private final AppInfo appInfo;
        private final AlbumRepo albumRepo;
        private final AlbumWorkingRepoExt albumWorkingRepoExt;
        private final AlbumStateHistoryRepoExt albumStateHistoryRepoExt;
        private final AmazonS3 amazonS3;
        private final CacheUtil cacheUtil;

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

            log.info("########## job 시작");

            while(true) {

                if (Boolean.FALSE.equals(cacheUtil.isJobEnabled())) {
                    break;
                };

                // 상태를 Deploying 으로 업데이트 하면서 대상 가져오기 limit 1
                AlbumWorking albumWorking = albumWorkingRepoExt.findOneConfirmedAndUpdate();
                if (albumWorking == null) {
                    break;
                }

                log.info(" 대상 불러오기 ");
                log.info("###### albumWorking : " + albumWorking.toString());

                try {
                    boolean isCompleted = true;
                    List<String> copyList = new ArrayList<>();
                    copyList.add(albumWorking.getThumbnailKey());
                    copyList.addAll(albumWorking.getReward().getFiles());
                    if (albumWorking.getCards() != null && albumWorking.getCards().size() > 0) {
                        for (AlbumCard card : albumWorking.getCards()) {
                            copyList.addAll(card.getContents().getFiles());
                        }
                    }

                    for (String s : copyList) {
                        log.info("file key : " + s);
                        if (s == null || s.trim().length() == 0) {
                            continue;
                        }

                        try {
                            // s3 에러 파일 정리 할것
                            // 버킷 파일 복사
                            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                                    appInfo.getBucket(),
                                    s,
                                    appInfo.getClientBucket(),
                                    keyPathAddVersion(s, albumWorking.getVersion())
                            );
                            //Copy
                            amazonS3.copyObject(copyObjRequest);

                        } catch (AmazonServiceException e) {
                            log.info("AmazonServiceException job error  albumWorking id : " + albumWorking.getId() );
                            log.info(" exception : " + e.getErrorCode());
                            if (e.getStatusCode() == 404) {
                                albumWorking = albumWorkingRepoExt.save(albumWorking.setState(AlbumState.REJECTED));
                                stateHistory(albumWorking, e.getErrorMessage());
                            } else {
                                stateHistory(albumWorking, "connection error try again");
                            }
                            isCompleted = false;
                            break;
                        } catch (SdkClientException e) {
                            // reject 처리
                            log.info("SdkClientException job error  albumWorking id : " + albumWorking.getId() );
                            e.printStackTrace();
                            albumWorking = albumWorkingRepoExt.save(albumWorking.setState(AlbumState.REJECTED));
                            stateHistory(albumWorking, e.getMessage());
                            isCompleted = false;
                            break;
                        }
                    }

                    if (isCompleted) {
                        log.info("#################################################");

                        // album에 save하기기
                        albumRepo.save(albumWorkingContentsPathAddVersion(albumWorking));

                        // 완료 상태 update
                        albumWorking = albumWorkingRepoExt.save(albumWorking.setState(AlbumState.DEPLOYED));
                        stateHistory(albumWorking, "");
                    }
                } catch (Exception e) {
                    albumWorking = albumWorkingRepoExt.save(albumWorking.setState(AlbumState.REJECTED));
                    stateHistory(albumWorking, e.getMessage());
                }
            }

            return RepeatStatus.FINISHED;
        }

        /**
         * album state history 에 history 추가
         * @param albumWorking
         * @param comment
         */
        private void stateHistory(AlbumWorking albumWorking, String comment) {

            StateHistory stateBatchHistory = StateHistory.builder()
                    .version(albumWorking.getVersion())
                    .state(albumWorking.getState())
                    .comment(comment)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy("BatchSystem")
                    .build();
            albumStateHistoryRepoExt.pushHistory(albumWorking.getId(), stateBatchHistory);
        }

        private String keyPathAddVersion(String key, int version) {
            if (key == null || key.equals("") || key.trim().equals("")) {
                return key;
            }
            String[] arr = key.split("/");
            String v = String.valueOf(version);
            String target = "albums";
            int position = Arrays.asList(arr).indexOf(target)+2;
            int newArrLength = arr.length + 1;
            String[] newArr = new String[newArrLength];
            for( int i = 0; i < newArrLength; i++) {
                if(i < position) {
                    newArr[i] = arr[i];
                } else if( i == position) {
                    newArr[i] = v;
                } else {
                    newArr[i] = arr[i - 1];
                }
            }

            return String.join("/", newArr);
        }

        private CardContents cardContentsPathAddVersion(CardContents cardContents, int version) {
            return CardContents.builder()
                    .title(cardContents.getTitle())
                    .type(cardContents.getType())
                    .description(cardContents.getDescription())
                    .thumbnailKey(keyPathAddVersion(cardContents.getThumbnailKey(), version))
                    .imageKey(keyPathAddVersion(cardContents.getImageKey(), version))
                    .videoKey(keyPathAddVersion(cardContents.getVideoKey(), version))
                    .build();
        }

        private Album albumWorkingContentsPathAddVersion(AlbumWorking albumWorking) {

            List<AlbumCard> cards = new ArrayList<>();

            for (AlbumCard card : albumWorking.getCards()) {
                cards.add(AlbumCard.builder()
                        .members(card.getMembers())
                        .id(card.getId())
                        .position(card.getPosition())
                        .contents(cardContentsPathAddVersion(card.getContents(), albumWorking.getVersion()))
                        .createdAt(card.getCreatedAt())
                        .operationType(card.getOperationType())
                        .tags(card.getTags())
                        .build());
            }

            return Album.builder()
                    .id(albumWorking.getId())
                    .tags(albumWorking.getTags())
                    .artistId(albumWorking.getArtistId())
                    .title(albumWorking.getTitle())
                    .description(albumWorking.getDescription())
                    .thumbnailKey(keyPathAddVersion(albumWorking.getThumbnailKey(), albumWorking.getVersion()))
                    .reward(cardContentsPathAddVersion(albumWorking.getReward(), albumWorking.getVersion()))
                    .version(albumWorking.getVersion())
                    .cards(cards)
                    .build();

        }
    }
}
