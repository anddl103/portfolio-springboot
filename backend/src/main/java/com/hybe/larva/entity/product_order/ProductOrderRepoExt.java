package com.hybe.larva.entity.product_order;

import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductOrderRepoExt {

    private final ProductOrderRepo repo;
    private final MongoOperations mongo;

    public ProductOrder insert(ProductOrder order) {
        return repo.insert(order);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(ProductOrder user) {
        repo.delete(user);
    }

    public ProductOrder save(ProductOrder order) {
        return repo.save(order);
    }

    public ProductOrder findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find ProductOrder: id=" + id));
    }

    public List<ProductOrder> findAllByNfcCardId(String nfcCardId) {
        final Criteria criteria = Criteria.where(ProductOrder.DELETED).ne(true);
        criteria.and(ProductOrder.NFC_CARD_ID).is(nfcCardId);
        criteria.and(ProductOrder.STATE).is(ProductOrderStatus.CREATED);
        Query query = new Query(criteria);
        return mongo.find(query, ProductOrder.class);
    }

    public ProductOrderAggregation findByIdDetail(String id) {
        final Criteria criteria = Criteria.where(ProductOrder.DELETED).ne(true);
        criteria.and(ProductOrder.ID).is(new ObjectId(id));

        MatchOperation defaultMatch = Aggregation.match(criteria);

        // productOrder -> nfcCard 매핑
        AddFieldsOperation addFields =
                Aggregation.addFields()
                        .addFieldWithValue(ProductOrder.AG_NFC_CARD_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_NFC_CARD_ID))
                        .addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_ADD_FIELD, ConvertOperators.ToString.toString(ProductOrder.AG_PRODUCT_KEY_ID))
                        .build();

        // lookup
        LookupOperation nfcCardLookup = Aggregation.lookup(
                ProductOrder.AG_NFC_CARD_FROM_COLLECTION, ProductOrder.AG_NFC_CARD_ADD_FIELD,
                ProductOrder.AG_NFC_CARD_FOREIGN_FIELD, ProductOrder.AG_NFC_CARD_AS);

        AddFieldsOperation nfcCardAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_NFC_CARD_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_NFC_CARD_AS).elementAt(0)
                        ).then("false")
                ).build();

        // nfcCard -> album 매핑
        AddFieldsOperation albumAddField =
                Aggregation.addFields()
                        .addFieldWithValue(ProductOrder.AG_ALBUM_ADD_FIELD,
                                ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_ALBUM_ID))
                        .addFieldWithValue(ProductOrder.AG_ARTIST_ADD_FIELD,
                                ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_ARTIST_ID)).build();


        // lookup
        LookupOperation albumLookup = Aggregation.lookup(
                ProductOrder.AG_ALBUM_FROM_COLLECTION, ProductOrder.AG_ALBUM_ADD_FIELD, ProductOrder.AG_ALBUM_FOREIGN_FIELD, ProductOrder.AG_ALBUM_AS);


        AddFieldsOperation albumAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_ALBUM_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_ALBUM_AS).elementAt(0)
                        ).then("false")
                ).build();

        LookupOperation artistLookup = Aggregation.lookup(
                ProductOrder.AG_ARTIST_FROM_COLLECTION, ProductOrder.AG_ARTIST_ADD_FIELD,
                ProductOrder.AG_ARTIST_FOREIGN_FIELD, ProductOrder.AG_ARTIST_AS);


        AddFieldsOperation artistAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_ARTIST_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_ARTIST_AS).elementAt(0)
                        ).then("false")
                ).build();

        LookupOperation productKeyLookup = Aggregation.lookup(
                ProductOrder.AG_PRODUCT_KEY_FROM_COLLECTION,
                ProductOrder.AG_PRODUCT_KEY_ADD_FIELD,
                ProductOrder.AG_PRODUCT_KEY_FOREIGN_FIELD,
                ProductOrder.AG_PRODUCT_KEY_AS);

        List<Object> verifiedConditions = new ArrayList<>();
        verifiedConditions.add("$$r.verified");
        verifiedConditions.add(true);

        List<Object> assignedConditions = new ArrayList<>();
        assignedConditions.add("$$r.assigned");
        assignedConditions.add(true);

        AddFieldsOperation productKeyAddFiledCount = Aggregation.addFields()
                .addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_VERIFIED_COUNT,
                        new Document("$size",
                                new Document("$filter",
                                        new Document("input","$"+ProductOrder.AG_PRODUCT_KEY_AS)
                                                .append("as", "r").append("cond", new Document("$eq", verifiedConditions))))
                ).addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_ASSIGNED_COUNT,
                        new Document("$size",
                                new Document("$filter",
                                        new Document("input","$"+ProductOrder.AG_PRODUCT_KEY_AS)
                                                .append("as", "r").append("cond", new Document("$eq", assignedConditions))))
                ).build();

        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, addFields, nfcCardLookup, nfcCardAddFieldArray, albumAddField,
                albumLookup, albumAddFieldArray, artistLookup, artistAddFieldArray, productKeyLookup, productKeyAddFiledCount);

        Optional<ProductOrderAggregation> productOrderAggregation =
                Optional.ofNullable(mongo.aggregate(aggregation, ProductOrder.class, ProductOrderAggregation.class)
                        .getUniqueMappedResult());

        return productOrderAggregation.orElseThrow(() ->
                new ResourceNotFoundException("Cannot find UserCard: id=" + id));
    }

    public Page<ProductOrderAggregation> search(Criteria defaultCriteria, Criteria criteria, Pageable pageable) {

        MatchOperation defaultMatch = Aggregation.match(defaultCriteria);

        MatchOperation match = Aggregation.match(criteria);

        // productOrder -> nfcCard 매핑
        AddFieldsOperation addFields =
                Aggregation.addFields()
                        .addFieldWithValue(ProductOrder.AG_NFC_CARD_ADD_FIELD, ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_NFC_CARD_ID))
                        .addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_ADD_FIELD, ConvertOperators.ToString.toString(ProductOrder.AG_PRODUCT_KEY_ID))
                        .build();

        // lookup
        LookupOperation nfcCardLookup = Aggregation.lookup(
                ProductOrder.AG_NFC_CARD_FROM_COLLECTION, ProductOrder.AG_NFC_CARD_ADD_FIELD,
                ProductOrder.AG_NFC_CARD_FOREIGN_FIELD, ProductOrder.AG_NFC_CARD_AS);

        AddFieldsOperation nfcCardAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_NFC_CARD_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_NFC_CARD_AS).elementAt(0)
                        ).then("false")
                ).build();

        // nfcCard -> album 매핑
        AddFieldsOperation albumAddField =
                Aggregation.addFields()
                        .addFieldWithValue(ProductOrder.AG_ALBUM_ADD_FIELD,
                                ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_ALBUM_ID))
                        .addFieldWithValue(ProductOrder.AG_ARTIST_ADD_FIELD,
                                ConvertOperators.ToObjectId.toObjectId(ProductOrder.AG_ARTIST_ID)).build();


        // lookup
        LookupOperation albumLookup = Aggregation.lookup(
                ProductOrder.AG_ALBUM_FROM_COLLECTION, ProductOrder.AG_ALBUM_ADD_FIELD,
                ProductOrder.AG_ALBUM_FOREIGN_FIELD, ProductOrder.AG_ALBUM_AS);


        AddFieldsOperation albumAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_ALBUM_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_ALBUM_AS).elementAt(0)
                        ).then("false")
                ).build();

        // nfcCard -> artist 매칭 추가.
        // lookup
        LookupOperation artistLookup = Aggregation.lookup(
                ProductOrder.AG_ARTIST_FROM_COLLECTION, ProductOrder.AG_ARTIST_ADD_FIELD,
                ProductOrder.AG_ARTIST_FOREIGN_FIELD, ProductOrder.AG_ARTIST_AS);


        AddFieldsOperation artistAddFieldArray = Aggregation.addFields()
                .addFieldWithValue(
                        ProductOrder.AG_ARTIST_AS, ConditionalOperators.ifNull(
                                ArrayOperators.ArrayElemAt.arrayOf(ProductOrder.AG_ARTIST_AS).elementAt(0)
                        ).then("false")
                ).build();


        // 컬럼 추가 ObjectId -> StringId로 변경
//        AddFieldsOperation cardAddField = Aggregation.addFields().addFieldWithValue(
//                ProductOrder.AG_CARD_ADD_FIELD, VariableOperators.mapItemsOf(ProductOrder.AG_CARD_ID)
//                        .as("r")
//                        .andApply(context ->
//                                ConvertOperators.ToObjectId.toObjectId("$$r").toDocument(context)
//                        )
//        ).build();
//
//        LookupOperation cardLookup = Aggregation.lookup(
//                ProductOrder.AG_CARD_FROM_COLLECTION, ProductOrder.AG_CARD_ADD_FIELD,
//                ProductOrder.AG_CARD_FOREIGN_FIELD, ProductOrder.AG_CARD_AS);


        LookupOperation productKeyLookup = Aggregation.lookup(
                ProductOrder.AG_PRODUCT_KEY_FROM_COLLECTION,
                ProductOrder.AG_PRODUCT_KEY_ADD_FIELD,
                ProductOrder.AG_PRODUCT_KEY_FOREIGN_FIELD,
                ProductOrder.AG_PRODUCT_KEY_AS);



        List<Object> verifiedConditions = new ArrayList<>();
        verifiedConditions.add("$$r.verified");
        verifiedConditions.add(true);

        List<Object> assignedConditions = new ArrayList<>();
        assignedConditions.add("$$r.assigned");
        assignedConditions.add(true);

        AddFieldsOperation productKeyAddFiledCount = Aggregation.addFields()
                .addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_VERIFIED_COUNT,
                    new Document("$size",
                        new Document("$filter",
                            new Document("input","$"+ProductOrder.AG_PRODUCT_KEY_AS)
                                    .append("as", "r").append("cond", new Document("$eq", verifiedConditions))))
                ).addFieldWithValue(ProductOrder.AG_PRODUCT_KEY_ASSIGNED_COUNT,
                    new Document("$size",
                        new Document("$filter",
                            new Document("input","$"+ProductOrder.AG_PRODUCT_KEY_AS)
                                .append("as", "r").append("cond", new Document("$eq", assignedConditions))))
                ).build();


        // 정렬
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, ProductOrder.CREATED_AT);

        // 건너뛰기
        SkipOperation skip = Aggregation.skip((pageable.getPageNumber()*pageable.getPageSize()));

        LimitOperation limit = Aggregation.limit(pageable.getPageSize());


        long total = getCount(defaultMatch, addFields, nfcCardLookup, nfcCardAddFieldArray, match, albumAddField,
                albumLookup, albumAddFieldArray, artistLookup, artistAddFieldArray, productKeyLookup,
                productKeyAddFiledCount);

        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, addFields, nfcCardLookup, nfcCardAddFieldArray, match, albumAddField,
                albumLookup, albumAddFieldArray, artistLookup, artistAddFieldArray, productKeyLookup,
                productKeyAddFiledCount, sort, skip, limit);

        List<ProductOrderAggregation> productOrderAggregations =
                mongo.aggregate(aggregation, ProductOrder.class, ProductOrderAggregation.class)
                        .getMappedResults();

        return new PageImpl<>(productOrderAggregations, pageable, total);
    }

    private long getCount(MatchOperation defaultMatch, AddFieldsOperation addFields, LookupOperation nfcCardLookup,
                          AddFieldsOperation nfcCardAddFieldArray, MatchOperation match, AddFieldsOperation albumAddField,
                          LookupOperation albumLookup, AddFieldsOperation albumAddFieldArray, LookupOperation artistLookup,
                          AddFieldsOperation artistAddFieldArray, LookupOperation productKeyLookup,
                          AddFieldsOperation productKeyAddFiledCount
    ) {

        GroupOperation group = Aggregation.group(ProductOrder.ID).count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                defaultMatch, addFields, nfcCardLookup, nfcCardAddFieldArray, match, albumAddField,
                albumLookup, albumAddFieldArray, artistLookup, artistAddFieldArray,
                productKeyLookup, productKeyAddFiledCount, group);

        return Optional.of(mongo.aggregate(aggregation, ProductOrder.class, ProductOrderAggregation.class)
                .getMappedResults().size()).orElse(0);
    }

}
