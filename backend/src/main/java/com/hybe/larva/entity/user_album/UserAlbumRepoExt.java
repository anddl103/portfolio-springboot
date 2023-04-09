package com.hybe.larva.entity.user_album;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.nfc_card.NfcCard;
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
public class UserAlbumRepoExt {

    private final UserAlbumRepo repo;
    private final MongoOperations mongo;

    // 확인 후 적용
//    private final AddFieldsOperation aFields = Aggregation.addFields().addFieldWithValue(
//            "cards", VariableOperators.mapItemsOf( "$range: [ 0, { $size: \"$cards\" } ] ")
//
//                    .as("r")
//                    .andApply(context ->
//                        new Document("oid", ArrayOperators.ArrayElemAt.arrayOf("cards.id").elementAt("r"))
//                        .append("position", ArrayOperators.ArrayElemAt.arrayOf("cards.position").elementAt("r"))
//                        .append("card", ArrayOperators.ArrayElemAt.arrayOf("as").elementAt("r"))
//                    )
//    ).build();

    public UserAlbum insert(UserAlbum userAlbum) {
        return repo.insert(userAlbum);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(UserAlbum userAlbum) {
        repo.delete(userAlbum);
    }

    public UserAlbum save(UserAlbum userAlbum) {
        return repo.save(userAlbum);
    }

    public UserAlbum findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find UserAlbum: id=" + id));
    }

    public UserAlbum findByAlbumIdCheck(String albumId, String uid) {
        return repo.findByUidAndAlbumId(uid, albumId).orElse(null);
    }

    public UserAlbum findByAlbumId(String userAlbumId, String uid) {
        final Criteria criteria = Criteria.where(UserAlbum.DELETED).ne(true);
        criteria.and(UserAlbum.UID).is(uid);
        criteria.and(UserAlbum.ID).is(userAlbumId);
        Query query = new Query(criteria);
        return Optional.ofNullable(mongo.findOne(query, UserAlbum.class)).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find UserAlbum: albumId=" + userAlbumId));
    }

    
    public UserAlbumAggregation findByIdDetail(String id) {
    
        final Criteria criteria = Criteria.where(UserAlbum.ID).is(id);
        criteria.and(UserAlbum.UID).is(CommonUser.getUid());
        
    
        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);
    
    
        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(UserAlbum.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(UserAlbum._ID)).build();
    
    
        LookupOperation lookup = Aggregation.lookup(
                UserAlbum.FROM_COLLECTION, UserAlbum.ADD_FIELD, UserAlbum.FOREIGN_FIELD, UserAlbum.AS);
    
        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        UserAlbum.AS, ArrayOperators.ArrayElemAt.arrayOf(UserAlbum.AS).elementAt(0)
                ).build();
    
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray);
    
    
        return Optional.of(
                mongo.aggregate(aggregation, UserAlbum.class, UserAlbumAggregation.class)
                        .getUniqueMappedResult()).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find userCards: id=" + id));
    
    }

    public Page<UserAlbumAggregation> search(Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);


        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(UserAlbum.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(UserAlbum._ID)).build();


        LookupOperation lookup = Aggregation.lookup(
                UserAlbum.FROM_COLLECTION, UserAlbum.ADD_FIELD, UserAlbum.FOREIGN_FIELD, UserAlbum.AS);

        AddFieldsOperation addFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        UserAlbum.AS, ArrayOperators.ArrayElemAt.arrayOf(UserAlbum.AS).elementAt(0)
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

        List<UserAlbumAggregation> aggregations =
                mongo.aggregate(aggregation, UserAlbum.class, UserAlbumAggregation.class)
                        .getMappedResults();

        return new PageImpl<>(aggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields, LookupOperation lookup, AddFieldsOperation addFieldArray) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup, addFieldArray, group);

        return Optional.of(
                mongo.aggregate(aggregation, UserAlbum.class, UserAlbumAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }


    public UserAlbum updateUserAlbumCardFavorite(String userCardId, String uid, boolean favorite) {
        Query query = new Query(Criteria.where(UserAlbum.UID).is(uid)
                .and(UserAlbum.CARD_ID).is(userCardId));

//        Update update = new Update().set("album", album);
        Update update = new Update().set("cards.$.favorite", favorite);

        return mongo.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), UserAlbum.class);
    }

    public UserAlbum updateUserAlbumCard(String uid, String userAlbumId,  UserAlbumCard userAlbumCard) {
        Query query = new Query(Criteria.where(UserAlbum.UID).is(uid)
                .and(UserAlbum.ID).is(userAlbumId));

//        Update update = new Update().set("album", album);
        Update update = new Update().push("cards", userAlbumCard);

        return mongo.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), UserAlbum.class);
    }



    public UserAlbum incCollectCount(String uid, String userAlbumId, String userCardId) {
        Query query = new Query(Criteria.where(UserAlbum.UID).is(uid)
                .and(UserAlbum.ID).is(userAlbumId)
                .and(UserAlbum.CARD_ID).is(userCardId));

        Update update = new Update().inc("cards.$.collectCount", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        return mongo.findAndModify(query, update, options, UserAlbum.class);
    }

    

    /**
     * migration 용
     * artist 추가
     */
    public void migrationModifyAlbum(String userArtistId, String userAlbumId) {
        Query query = new Query(Criteria.where(UserAlbum.ID).is(userAlbumId));
//        Update update = new Update().set("album", album);
        Update update = new Update().set("userArtistId", userArtistId);
        mongo.updateMulti(query, update, UserAlbum.class);
    }

    /**
     * migration 용
     * artisrId 삭제
     */
    public void migrationDeleteAlbumId() {
        Query query = new Query();
        Update update = new Update().unset("albumId");
        mongo.updateMulti(query, update, UserAlbum.class);
    }

    /**
     * 마이그레이션에서 사용
     * @return
     */
    public List<UserAlbum> findAll(){
        final Criteria criteria = Criteria.where(UserAlbum.DELETED).ne(true);
        Query query = new Query(criteria);
        List<UserAlbum> userAlbums = mongo.find(query, UserAlbum.class);
        return userAlbums;
    }


}
