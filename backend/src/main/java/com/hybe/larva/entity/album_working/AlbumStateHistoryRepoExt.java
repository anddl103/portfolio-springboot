package com.hybe.larva.entity.album_working;

import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlbumStateHistoryRepoExt {

    private final AlbumStateHistoryRepo repo;
    private final MongoOperations mongo;

    public AlbumStateHistory insert(AlbumStateHistory user) {
        return repo.insert(user);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(AlbumStateHistory user) {
        repo.delete(user);
    }

    public AlbumStateHistory save(AlbumStateHistory album) {
        return repo.save(album);
    }

    public AlbumStateHistory findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find AlbumAdditionalInfo : id=" + id));
    }

    public List<AlbumStateHistory> findByAlbumId(String albumId) {
        return repo.findByAlbumId(albumId);
    }


    public AlbumStateHistory pushHistory(String albumId, StateHistory stateHistory) {
        Query query = new Query(Criteria.where(AlbumStateHistory.ALBUM_ID).is(albumId));
        Update update = new Update().push("history", stateHistory);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongo.findAndModify(query, update, options, AlbumStateHistory.class);
    }

    public StateHistory findHistoryVersionAndState(String albumId, int version, AlbumState state) {

        final Criteria defaultCriteria = Criteria.where(AlbumStateHistory.ALBUM_ID).is(albumId);
        final Criteria criteria = Criteria.where(AlbumStateHistory.VERSION).is(version);
        criteria.and(AlbumStateHistory.STATE).is(state.name());

        // 검색 조건
        MatchOperation defaultMatch = Aggregation.match(defaultCriteria);

        UnwindOperation unWind = Aggregation.unwind("$history");

        ProjectionOperation project = Aggregation.project("history");

        ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("$history");

        MatchOperation match = Aggregation.match(criteria);

        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, unWind, project, replaceRoot, match);

        return mongo.aggregate(aggregation, AlbumStateHistory.class, StateHistory.class)
                        .getUniqueMappedResult();
    }



    public Page<StateHistory> searchHistory(Criteria defaultCriteria, Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation defaultMatch = Aggregation.match(defaultCriteria);

        UnwindOperation unWind = Aggregation.unwind("$history");

        ProjectionOperation project = Aggregation.project("history");

        ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("$history");

        MatchOperation match = Aggregation.match(criteria);

        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, AlbumStateHistory.UPDATE_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip(pageable.getOffset());

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, unWind, project, replaceRoot, match, sort, skip, limit);

        long total = getCount(defaultMatch, unWind, project, replaceRoot, match);

        List<StateHistory> albumAggregations =
                mongo.aggregate(aggregation, AlbumStateHistory.class, StateHistory.class)
                        .getMappedResults();

        log.info("############ list :" + albumAggregations.toString());
        return new PageImpl<>(albumAggregations, pageable, total);
    }

    private long getCount(MatchOperation defaultMatch, UnwindOperation unWind, ProjectionOperation project, ReplaceRootOperation replaceRoot,
                          MatchOperation match) {

        GroupOperation group = Aggregation.group(AlbumStateHistory.VERSION).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, unWind, project, replaceRoot, match, group);

        return Optional.of(
                mongo.aggregate(aggregation, AlbumStateHistory.class, StateHistory.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }

}
