package com.hybe.larva.entity.product_key_history;

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
public class ProductKeyHistoryRepoExt {

    private final ProductKeyHistoryRepo repo;
    private final MongoOperations mongo;

    public ProductKeyHistory insert(ProductKeyHistory productKeyHistory) {
        return repo.insert(productKeyHistory);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(ProductKeyHistory productKeyHistory) {
        repo.delete(productKeyHistory);
    }

    public ProductKeyHistory save(ProductKeyHistory productKeyHistory) {
        return repo.save(productKeyHistory);
    }



    public Page<ProductKeyHistoryAggregation> searchUid(Criteria criteria, Pageable pageable, String uid) {

        final Criteria defaultCriteria = Criteria.where(ProductKeyHistory.DELETED).ne(true);
        defaultCriteria.and(ProductKeyHistory.UID).is(uid);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip((pageable.getPageNumber()*pageable.getPageSize()));

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());

        return getResult(defaultCriteria, criteria, pageable, skip, limit);
    }

    private Page<ProductKeyHistoryAggregation> getResult(Criteria defaultCriteria, Criteria criteria, Pageable pageable,
                                                SkipOperation skip, LimitOperation limit)
    {
        // 검색 조건
        MatchOperation defaultMatch = Aggregation.match(defaultCriteria);

        // 컬럼 추가 ObjectId -> StringId로 변경
        AddFieldsOperation addFields =
                Aggregation.addFields()
                        .addFieldWithValue(ProductKeyHistory.ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(ProductKeyHistory._ID)).build();

        LookupOperation lookup = Aggregation.lookup(
                ProductKeyHistory.FROM_COLLECTION, ProductKeyHistory.ADD_FIELD, ProductKeyHistory.FOREIGN_FIELD, ProductKeyHistory.AS);

        // 검색 조건
        MatchOperation match = Aggregation.match(criteria);

        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, ProductKeyHistory.CREATED_AT);


        AddFieldsOperation addFields2 = Aggregation.addFields()
                .addFieldWithValue(
                        ProductKeyHistory.AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductKeyHistory.AS).elementAt(0)
                        ).then("false")
                ).build();

        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, addFields, lookup, match, addFields2,   sort, skip, limit);

        long total = getCount(defaultMatch, addFields, lookup, match);

        log.info(" aggregation page total : " + total );

        //AggregationResults<UserCardSearchResponse> userCards = mongo.aggregate(aggregation, Card.class, UserCard.class);
        List<ProductKeyHistoryAggregation> productKeyHistoryAggregations =
                mongo.aggregate(aggregation, ProductKeyHistory.class, ProductKeyHistoryAggregation.class)
                        .getMappedResults();

        log.info("list : " + productKeyHistoryAggregations);

        return new PageImpl<>(productKeyHistoryAggregations, pageable, total);
    }

    private long getCount(MatchOperation defaultMatch, AddFieldsOperation addFields, LookupOperation lookup,
                          MatchOperation match) {

        GroupOperation group = Aggregation.group(ProductKeyHistory.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, addFields, lookup, match, group);


        return Optional.of(mongo.aggregate(aggregation, ProductKeyHistory.class, ProductKeyHistoryAggregation.class)
                .getMappedResults().size()).orElse(0);
    }
}
