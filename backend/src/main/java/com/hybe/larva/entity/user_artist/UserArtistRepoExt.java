package com.hybe.larva.entity.user_artist;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserArtistRepoExt {

    private final UserArtistRepo repo;
    private final MongoOperations mongo;

    public UserArtist insert(UserArtist userArtist) {
        return repo.insert(userArtist);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(UserArtist userArtist) {
        repo.delete(userArtist);
    }

    public UserArtist save(UserArtist userArtist) {
        return repo.save(userArtist);
    }

    public UserArtist findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find userArtist: id=" + id));
    }

    public UserArtist findByArtistId(String artistId, String uid) {
        return repo.findByUidAndArtistId(uid, artistId);
    }

//    public UserArtist findByIdDetail(String id) {
//        return repo.findByIdAndUid(id, CommonUser.getUid()).orElseThrow(() ->
//                new ResourceNotFoundException("Cannot find userArtist: id=" + id));
//    }
//
//    public Page<UserArtist> search(Criteria criteria, Pageable pageable) {
//
//        Query query = new Query(criteria).with(pageable);
//        List<UserArtist> artists = mongo.find(query, UserArtist.class);
//        return PageableExecutionUtils.getPage(artists, pageable,
//                () -> mongo.count(new Query(criteria), UserArtist.class)
//        );
//    }

    public UserArtistAggregation findByIdDetail(String id) {

        final Criteria criteria = Criteria.where(UserArtist.ID).is(id);
        criteria.and(UserArtist.UID).is(CommonUser.getUid());
//        criteria.and(UserArtist.COLLECT_COUNT).gte(collectCount);

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);


        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(UserArtist.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(UserArtist._ID)).build();


        LookupOperation lookup = Aggregation.lookup(
                UserArtist.FROM_COLLECTION, UserArtist.ADD_FIELD, UserArtist.FOREIGN_FIELD, UserArtist.AS);

        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        UserArtist.AS, ArrayOperators.ArrayElemAt.arrayOf(UserArtist.AS).elementAt(0)
                ).build();

        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray);


        return Optional.of(
                mongo.aggregate(aggregation, UserArtist.class, UserArtistAggregation.class)
                        .getUniqueMappedResult()).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find userArtist: id=" + id));

    }


    public Page<UserArtistAggregation> search(Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);


        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(UserArtist.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(UserArtist._ID)).build();


        LookupOperation lookup = Aggregation.lookup(
                UserArtist.FROM_COLLECTION, UserArtist.ADD_FIELD, UserArtist.FOREIGN_FIELD, UserArtist.AS);

        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        UserArtist.AS, ArrayOperators.ArrayElemAt.arrayOf(UserArtist.AS).elementAt(0)
                ).build();

        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, NfcCard.CREATED_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip(pageable.getOffset());

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray, sort, skip,
                limit);

        long total = getCount(match, addFields, lookup, addFieldArray);

        List<UserArtistAggregation> aggregations =
                mongo.aggregate(aggregation, UserArtist.class, UserArtistAggregation.class)
                        .getMappedResults();

        return new PageImpl<>(aggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields, LookupOperation lookup, AddFieldsOperation addFieldArray) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray, group);

        return Optional.of(
                mongo.aggregate(aggregation, UserArtist.class, UserArtistAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }
}
