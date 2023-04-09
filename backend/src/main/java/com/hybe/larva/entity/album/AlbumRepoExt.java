package com.hybe.larva.entity.album;

import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class AlbumRepoExt {

    private final AlbumRepo repo;
    private final MongoOperations mongo;

    public Album insert(Album user) {
        return repo.insert(user);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Album user) {
        repo.delete(user);
    }

    public Album save(Album album) {
        return repo.save(album);
    }

    public Album findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find album: id=" + id));
    }

    public List<Album> findAllByArtistId(String artistId) {
        return repo.findByArtistId(artistId);
    }


    public boolean findByArtistId(String artistId) {
        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);
        criteria.and(Album.ARTIST_ID).is(artistId);
        Query query = new Query(criteria);
        return mongo.exists(query, Album.class);
    }

    /**
     * 마이그레이션에서 사용
     * @return
     */
    public List<Album> findAll(){
        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);
        criteria.and(Album.ID).ne(new ObjectId("613578a9380894085fb8757d"));
        Query query = new Query(criteria);
        List<Album> albums = mongo.find(query, Album.class);
        return albums;
    }

    public AlbumAggregation findByIdDetail(String id) {

        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);
        criteria.and(Album.ID).is(new ObjectId(id));

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(Album.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(Album._ID)).build();


        LookupOperation lookup = Aggregation.lookup(
                Album.FROM_COLLECTION, Album.ADD_FIELD, Album.FOREIGN_FIELD, Album.AS);

        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        Album.AS, ArrayOperators.ArrayElemAt.arrayOf(Album.AS).elementAt(0)
                ).build();


        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray);

        Optional<AlbumAggregation> albumAggregation =
                Optional.ofNullable(mongo.aggregate(aggregation, Album.class, AlbumAggregation.class)
                        .getUniqueMappedResult());

        return albumAggregation.orElseThrow(() ->
                new ResourceNotFoundException("Cannot find UserCard: id=" + id));
    }

    public Page<AlbumAggregation> search(Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        // 컬럼 추가 ObjectId -> StringId로 변경
        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(Album.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(Album._ID)).build();


        LookupOperation lookup = Aggregation.lookup(
                Album.FROM_COLLECTION, Album.ADD_FIELD, Album.FOREIGN_FIELD, Album.AS);

        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        Album.AS, ArrayOperators.ArrayElemAt.arrayOf(Album.AS).elementAt(0)
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

        List<AlbumAggregation> albumAggregations =
                mongo.aggregate(aggregation, Album.class, AlbumAggregation.class)
                        .getMappedResults();

        return new PageImpl<>(albumAggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields, LookupOperation lookup, AddFieldsOperation addFieldArray) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray, group);

        return Optional.of(
                mongo.aggregate(aggregation, Album.class, AlbumAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }

    public void migrationCards(String albumId) {
        Query query = Query.query(Criteria.where(Album.ID).is(albumId));

        Update update = new Update().unset("cards");
        mongo.updateMulti(query, update, Album.class);
    }

    /**
     * migration 용
     * searchTags-> tags 변경
     */
    public void migrationModifyTags() {
        Query query = new Query();

        Update update = new Update().rename("searchTags","tags");
//        update.unset("searchTags");
        mongo.updateMulti(query, update, Album.class);
    }

    public void updateCardMembers(String languagePackId) {
        Query query = new Query();
        Update update = new Update().pull("cards.members",languagePackId);
        mongo.updateMulti(query, update, Album.class);
    }

    public Album findByExists(String id) {
        final Criteria criteria = Criteria.where(Album.ID).is(id);
        Query query = new Query(criteria);
        return mongo.findOne(query, Album.class);
    }
}
