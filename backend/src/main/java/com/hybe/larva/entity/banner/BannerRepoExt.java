package com.hybe.larva.entity.banner;

import com.hybe.larva.entity.album_working.AlbumWorking;
import com.hybe.larva.entity.album_working.AlbumWorkingAggregation;
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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BannerRepoExt {

    private final BannerRepo repo;
    private final MongoOperations mongo;

    public Banner insert(Banner banner) {
        return repo.insert(banner);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Banner banner) {
        repo.delete(banner);
    }

    public Banner save(Banner banner) {
        return repo.save(banner);
    }

    public Banner findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find banner: id=" + id));
    }

    public Page<Banner> searchUser(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<Banner> banners = mongo.find(query, Banner.class);
        return PageableExecutionUtils.getPage(banners, pageable,
                () -> mongo.count(new Query(criteria), Banner.class)
        );
    }

    public void decSortOrder(Integer sortOrder) {
        Query query = Query.query(Criteria.where(Banner.SORT_ORDER).gte(sortOrder)
                .and(Banner.DISPLAY).is(true)
                .and(Banner.DELETED).ne(true));
        int dec = -1;
        Update update = new Update().inc(Banner.SORT_ORDER, dec);
        mongo.updateMulti(query, update, Banner.class);
    }

    public int getMaxSortOrder() {

        final Criteria criteria = Criteria.where(Banner.DELETED).ne(true);
        criteria.and(Banner.DISPLAY).ne(false);

        MatchOperation match = Aggregation.match(criteria);

        GroupOperation group = Aggregation.group().max("sortOrder").as("maxSortOrder");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        int maxSortOrder = Optional.ofNullable(mongo.aggregate(aggregation, Banner.class, BannerMaxSortOrder.class)
                .getUniqueMappedResult()).map(BannerMaxSortOrder::getMaxSortOrder).orElse(0);
        return maxSortOrder+1;
    }

    public List<Banner> findByAlbumId(String albumId) {
        return repo.findAllByAlbumId(albumId);
    }


    public Page<BannerAggregation> search(Criteria criteria, Pageable pageable) {

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        // 컬럼 추가 ObjectId -> StringId로 변경
        // 확인 후 적용
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue(Banner.AG_ARTIST_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(Banner.AG_ARTIST_ID))
                .build();


        LookupOperation lookupArtist = Aggregation.lookup(
                Banner.AG_ARTIST_FROM_COLLECTION, Banner.AG_ARTIST_ADD_FIELD, Banner.AG_ARTIST_FOREIGN_FIELD, Banner.AG_ARTIST_AS);

        AddFieldsOperation addFieldArrayArtist = Aggregation.addFields()
                .addFieldWithValue(
                        Banner.AG_ARTIST_AS, ArrayOperators.ArrayElemAt.arrayOf(Banner.AG_ARTIST_AS).elementAt(0)
                ).build();

        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, Banner.CREATED_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip(pageable.getOffset());

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookupArtist, addFieldArrayArtist, sort, skip,
                limit);

        long total = getCount(match, addFields, lookupArtist, addFieldArrayArtist);

        List<BannerAggregation> albumAggregations =
                mongo.aggregate(aggregation, Banner.class, BannerAggregation.class)
                        .getMappedResults();

        log.info("############ list :" + albumAggregations.toString());
        return new PageImpl<>(albumAggregations, pageable, total);
    }

    private long getCount(MatchOperation match, AddFieldsOperation addFields, LookupOperation lookupArtist, AddFieldsOperation addFieldArrayArtist) {

        GroupOperation group = Aggregation.group(NfcCard.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                match, addFields, lookupArtist, addFieldArrayArtist, group);

        return Optional.of(
                mongo.aggregate(aggregation, Banner.class, BannerAggregation.class)
                        .getMappedResults()
                        .size()).orElse(0);
    }

}
