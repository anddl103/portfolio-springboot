package com.hybe.larva.entity.artist;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ArtistRepoExt {

    private final ArtistRepo repo;
    private final MongoOperations mongo;

    public Artist insert(Artist artist) {
        return repo.insert(artist);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public Artist delete(Artist artist) {
        artist = artist.delete();
        return repo.save(artist);
    }

    public Artist save(Artist artist) {
        return repo.save(artist);
    }

    public Artist findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cannot find artist: id=" + id));
    }

    public Artist findByName(String name, String id) {
        final Criteria criteria = Criteria.where(Artist.DELETED).ne(true);
        criteria.and(Artist.NAME).is(name);
        if (id != null) {
            criteria.and(Artist.ID).ne(id);
        }
        Query query = new Query(criteria);

        return mongo.findOne(query, Artist.class);
    }


    public Page<Artist> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<Artist> artists = mongo.find(query, Artist.class);
        return PageableExecutionUtils.getPage(artists, pageable,
                () -> mongo.count(new Query(criteria), Artist.class)
        );
    }

    public void decSortOrder(Integer sortOrder) {
        Query query = Query.query(Criteria.where(Artist.SORT_ORDER).gte(sortOrder)
                .and(Artist.DISPLAY).is(true)
                .and(Artist.DELETED).ne(true));
        int dec = -1;
        Update update = new Update().inc(Artist.SORT_ORDER, dec);
        mongo.updateMulti(query, update, Artist.class);
    }

    public int getMaxSortOrder() {

        final Criteria criteria = Criteria.where(Artist.DELETED).ne(true);
        criteria.and(Artist.DISPLAY).is(true);

        MatchOperation match = Aggregation.match(criteria);

        GroupOperation group = Aggregation.group().max("sortOrder").as("maxSortOrder");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        int maxSortOrder = Optional.ofNullable(mongo.aggregate(aggregation, Artist.class, ArtistOrderAggregation.class)
                .getUniqueMappedResult()).map(ArtistOrderAggregation::getMaxSortOrder).orElse(0);
        return maxSortOrder + 1;
    }

    public ArtistAggregation findByDetail(String id) {

        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);
        criteria.and(Artist.ID).is(new ObjectId(id));

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(
                        "mId", VariableOperators.mapItemsOf("$members")
                                .as("r")
                                .andApply(context ->
                                        ConvertOperators.ToObjectId.toObjectId("$$r.name").toDocument(context)
                                )
                )
                .build();

        LookupOperation lookup = Aggregation.lookup(
                "languagePacks", "mId", "_id", "as");

//                Album.FROM_COLLECTION, Album.ADD_FIELD, Album.FOREIGN_FIELD, Album.AS);


        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookup);

        Optional<ArtistAggregation> artistAggregation =
                Optional.ofNullable(mongo.aggregate(aggregation, Artist.class, ArtistAggregation.class)
                        .getUniqueMappedResult());

        return artistAggregation.orElseThrow(() ->
                new ResourceNotFoundException("Cannot find artist: id=" + id));
    }

    /**
     * migration 용
     * @param artistId
     * @param artistMembers
     */
    public void migrationArtistMember(String artistId, List<ArtistMember> artistMembers) {
        Query query = Query.query(Criteria.where(Artist.ID).is(artistId));

        Update update = new Update().set("members", artistMembers);
//        update.unset("searchTags");
        mongo.updateMulti(query, update, Artist.class);
    }

    /**
     * migration 용
     * searchTags-> tags 변경
     */
    public void migrationModifyMembers() {
        Query query = new Query();

        Update update = new Update().rename("members","oldMembers");
//        update.unset("searchTags");
        mongo.updateMulti(query, update, Artist.class);
    }

}
