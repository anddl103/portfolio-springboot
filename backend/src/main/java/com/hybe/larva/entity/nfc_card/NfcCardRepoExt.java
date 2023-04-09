package com.hybe.larva.entity.nfc_card;


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
public class NfcCardRepoExt {

    private final NfcCardRepo repo;
    private final MongoOperations mongo;

    public NfcCard insert(NfcCard nfcCard) {
        return repo.insert(nfcCard);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(NfcCard nfcCard) {
        repo.delete(nfcCard);
    }

    public NfcCard save(NfcCard nfcCard) {
        return repo.save(nfcCard);
    }


    public NfcCard findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find NfcCard: id=" + id));
    }

    public List<NfcCard> findByAlbumId(String albumId) {
        return repo.findAllByAlbumId(albumId);
    }

//    public Page<NfcCard> search(Criteria criteria, Pageable pageable) {
//        Query query = new Query(criteria).with(pageable);
//        List<NfcCard> nfcCards = mongo.find(query, NfcCard.class);
//        return PageableExecutionUtils.getPage(nfcCards, pageable,
//                () -> mongo.count(new Query(criteria), NfcCard.class)
//        );
//    }

    public NfcCardAggregation findByIdDetail(String id) {

        final Criteria criteria = Criteria.where(NfcCard.DELETED).ne(true);
        criteria.and(NfcCard.ID).is(new ObjectId(id));

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);


        // 컬럼 추가 ObjectId -> StringId로 변경
        AddFieldsOperation addFields = Aggregation.addFields()
//            .addFieldWithValue(
//                NfcCard.CARD_AS, VariableOperators.mapItemsOf(NfcCard.CARD_ID)
//                    .as("r")
//                    .andApply(context ->
//                            ConvertOperators.ToObjectId.toObjectId("$$r").toDocument(context)
//                    )
//            )
            .addFieldWithValue(NfcCard.ALBUM_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId("$"+NfcCard.ALBUM_ID))
            .build();


//        LookupOperation lookup = Aggregation.lookup(
//                NfcCard.CARD_AS, NfcCard.CARD_AS, NfcCard.CARD_FOREIGN_FIELD, NfcCard.CARD_AS);


        LookupOperation albumLookup = Aggregation.lookup(
                NfcCard.ALBUM_FROM_COLLECTION, NfcCard.ALBUM_ADD_FIELD, NfcCard.ALBUM_FOREIGN_FIELD, NfcCard.ALBUM_AS);

        AddFieldsOperation addFieldAlbumArray = Aggregation.addFields()
                .addFieldWithValue(
                        NfcCard.ALBUM_AS, ArrayOperators.ArrayElemAt.arrayOf(NfcCard.ALBUM_AS).elementAt(0)
                ).build();


        AddFieldsOperation addFieldArtist = Aggregation.addFields()
                .addFieldWithValue(
                    NfcCard.ARTIST_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(NfcCard.AG_ARTIST_ID)
                ).build();

        LookupOperation artistLookup = Aggregation.lookup(
                NfcCard.ARTIST_FROM_COLLECTION, NfcCard.ARTIST_ADD_FIELD, NfcCard.ARTIST_FOREIGN_FIELD, NfcCard.ARTIST_AS);

        AddFieldsOperation addFieldArtistArray = Aggregation.addFields()
                .addFieldWithValue(
                        NfcCard.ARTIST_AS, ArrayOperators.ArrayElemAt.arrayOf(NfcCard.ARTIST_AS).elementAt(0)
                ).build();


        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, albumLookup, addFieldAlbumArray, addFieldArtist, artistLookup, addFieldArtistArray);

        Optional<NfcCardAggregation> nfcCardAggregation =
                Optional.ofNullable(mongo.aggregate(aggregation, NfcCard.class, NfcCardAggregation.class)
                        .getUniqueMappedResult());

        return nfcCardAggregation.orElseThrow(() ->
                new ResourceNotFoundException("Cannot find UserCard: id=" + id));
    }

    public Page<NfcCardAggregation> search(Criteria criteria, Pageable pageable) {


        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        // 컬럼 추가 ObjectId -> StringId로 변경
        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
//            .addFieldWithValue(
//                NfcCard.CARD_AS, VariableOperators.mapItemsOf(NfcCard.CARD_ID)
//                .as("r")
//                .andApply(context ->
//                        ConvertOperators.ToObjectId.toObjectId("$$r").toDocument(context)
//                )
//            )
                .addFieldWithValue(NfcCard.ALBUM_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId("$"+NfcCard.ALBUM_ID))
            .build();

//        LookupOperation lookup = Aggregation.lookup(
//                NfcCard.CARD_AS, NfcCard.CARD_AS, NfcCard.CARD_FOREIGN_FIELD, NfcCard.CARD_AS);

        LookupOperation albumLookup = Aggregation.lookup(
                NfcCard.ALBUM_FROM_COLLECTION, NfcCard.ALBUM_ADD_FIELD, NfcCard.ALBUM_FOREIGN_FIELD, NfcCard.ALBUM_AS);

        AddFieldsOperation addFieldAlbumArray = Aggregation.addFields()
                .addFieldWithValue(
                        NfcCard.ALBUM_AS, ArrayOperators.ArrayElemAt.arrayOf(NfcCard.ALBUM_AS).elementAt(0)
                ).build();


        AddFieldsOperation addFieldArtist = Aggregation.addFields()
                .addFieldWithValue(
                        NfcCard.ARTIST_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(NfcCard.AG_ARTIST_ID)
                ).build();

        LookupOperation artistLookup = Aggregation.lookup(
                NfcCard.ARTIST_FROM_COLLECTION, NfcCard.ARTIST_ADD_FIELD, NfcCard.ARTIST_FOREIGN_FIELD, NfcCard.ARTIST_AS);

        AddFieldsOperation addFieldArtistArray = Aggregation.addFields()
                .addFieldWithValue(
                        NfcCard.ARTIST_AS, ArrayOperators.ArrayElemAt.arrayOf(NfcCard.ARTIST_AS).elementAt(0)
                ).build();


        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, NfcCard.CREATED_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip(pageable.getOffset());

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, albumLookup, addFieldAlbumArray,
                addFieldArtist, artistLookup, addFieldArtistArray, sort, skip,
                limit);

        long total = getCount(match, addFields, albumLookup, addFieldAlbumArray, addFieldArtist, artistLookup, addFieldArtistArray);

        log.info(" aggregation page total : " + total );

        List<NfcCardAggregation> userCardAggregations =
                mongo.aggregate(aggregation, NfcCard.class, NfcCardAggregation.class)
                        .getMappedResults();

        log.info("list : " + userCardAggregations);

        return new PageImpl<>(userCardAggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields,
                          LookupOperation albumLookup, AddFieldsOperation addFieldAlbumArray, AddFieldsOperation addFieldArtist,
                          LookupOperation artistLookup, AddFieldsOperation addFieldArtistArray) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, albumLookup, addFieldAlbumArray, addFieldArtist,
                artistLookup, addFieldArtistArray, group);

        return Optional.of(
                mongo.aggregate(aggregation, NfcCard.class, NfcCardAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }

    /**
     * migration 용
     * searchTags-> tags 변경
     */
    public void migrationModifyTags() {
        Query query = new Query();
        Update update = new Update().rename("searchTags","tags");
        mongo.updateMulti(query, update, NfcCard.class);
    }


    /**
     * migration 용
     * searchTags-> tags 변경
     */
    public void migrationModifyCardId(String cardId, String oldCardId) {
        Query query = new Query(Criteria.where("cardIds").is(oldCardId));
        Update update = new Update().push("cardIds", cardId);
        mongo.updateMulti(query, update, NfcCard.class);


        query = new Query(Criteria.where("cardIds").is(oldCardId));
        update = new Update().pull("cardIds", oldCardId);
        mongo.updateMulti(query, update, NfcCard.class);
    }
}
