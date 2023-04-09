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

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlbumWorkingRepoExt {

    private final AlbumWorkingRepo repo;
    private final MongoOperations mongo;

    public AlbumWorking insert(AlbumWorking user) {
        return repo.insert(user);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(AlbumWorking user) {
        repo.delete(user);
    }

    public AlbumWorking save(AlbumWorking album) {
        return repo.save(album);
    }

    public AlbumWorking findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find album: id=" + id));
    }

    public Page<AlbumWorkingAggregation> search(Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        // 컬럼 추가 ObjectId -> StringId로 변경
        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(AlbumWorking.AG_ARTIST_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(AlbumWorking.AG_ARTIST_ID))
                .build();


        LookupOperation lookupArtist = Aggregation.lookup(
                AlbumWorking.AG_ARTIST_FROM_COLLECTION, AlbumWorking.AG_ARTIST_ADD_FIELD, AlbumWorking.AG_ARTIST_FOREIGN_FIELD, AlbumWorking.AG_ARTIST_AS);

        AddFieldsOperation addFieldArrayArtist = Aggregation.addFields()
                .addFieldWithValue(
                        AlbumWorking.AG_ARTIST_AS, ArrayOperators.ArrayElemAt.arrayOf(AlbumWorking.AG_ARTIST_AS).elementAt(0)
                ).build();

        LookupOperation lookupAlbum = Aggregation.lookup(
                AlbumWorking.AG_ALBUM_FROM_COLLECTION, AlbumWorking.AG_ALBUM_FOREIGN_FIELD, AlbumWorking.AG_ALBUM_FOREIGN_FIELD, AlbumWorking.AG_ALBUM_AS);

        AddFieldsOperation addFieldAlbum = Aggregation.addFields()
                .addFieldWithValue(
                        AlbumWorking.AG_ALBUM_DEPLOYED_FLAG,
                        ArrayOperators.In.arrayOf(AlbumWorking.AG_ALBUM_ARRAY_OF).containsValue("$"+AlbumWorking.AG_ALBUM_FOREIGN_FIELD)
                ).build();

        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, AlbumWorking.CREATED_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip(pageable.getOffset());

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookupArtist, addFieldArrayArtist, lookupAlbum, addFieldAlbum, sort, skip,
                limit);

        long total = getCount(match, addFields, lookupArtist, addFieldArrayArtist);

        List<AlbumWorkingAggregation> albumAggregations =
                mongo.aggregate(aggregation, AlbumWorking.class, AlbumWorkingAggregation.class)
                        .getMappedResults();

        log.info("############ list :" + albumAggregations.toString());
        return new PageImpl<>(albumAggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields, LookupOperation lookupArtist, AddFieldsOperation addFieldArrayArtist) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookupArtist, addFieldArrayArtist, group);

        return Optional.of(
                mongo.aggregate(aggregation, AlbumWorking.class, AlbumWorkingAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }


    public AlbumWorking incVersion(String albumId) {
        Query query = new Query(Criteria.where(AlbumWorking.ID).is(albumId));
        Update update = new Update().inc("version", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongo.findAndModify(query, update, options, AlbumWorking.class);
    }


    public AlbumWorking findOneConfirmedAndUpdate() {
        Query query = new Query(Criteria.where(AlbumWorking.STATE).is(AlbumState.CONFIRMED))
                .with(Sort.by(Sort.Order.desc(AlbumWorking.UPDATED_AT)))
                .limit(1);

        Update update = new Update().set(AlbumWorking.STATE, AlbumState.DEPLOYING);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongo.findAndModify(query, update, options, AlbumWorking.class);
    }

    public boolean findDeployingCount() {
        Query query = new Query(Criteria.where(AlbumWorking.STATE).is(AlbumState.DEPLOYING));
        return mongo.count(query,AlbumWorking.class) > 0 ? true : false;
    }

}
