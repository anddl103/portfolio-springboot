package com.hybe.larva.config.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import java.util.Arrays;
import java.util.List;


public class CustomProjectAggregationOperation implements AggregationOperation {
    private String jsonOperation;

    public CustomProjectAggregationOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }

//    @Override
//    public Document toDocument(AggregationOperationContext aggregationOperationContext) {
//        return aggregationOperationContext.getMappedObject(Document.parse(jsonOperation));
//    }

//    @Override
    public Document toDocument(AggregationOperationContext context) {
        return null;
    }

    @Override
    public List<Document> toPipelineStages(AggregationOperationContext context) {
        return Arrays.asList(context.getMappedObject(Document.parse(jsonOperation)));
    }
}
