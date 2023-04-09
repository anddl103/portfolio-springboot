package com.hybe.larva.entity.common;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

    public static final String ID = "id";
    public static final String CREATED_AT = "createdAt";
    public static final String CREATED_BY = "createdBy";
//    public static final String UPDATED_AT = "updatedAt";
//    public static final String UPDATED_BY = "updatedBy";
    public static final String DELETED = "deleted";

    // auto generated
    @Setter
    @Id
    protected String id;

    // created date
    @CreatedDate
    @Indexed
    protected LocalDateTime createdAt;

    // created by (audit)
    @CreatedBy
    protected String createdBy;

    // updated date, If the deleted value is true, it means the deleted date.
//    @LastModifiedDate
//    protected LocalDateTime updatedAt;
//
//    // updated by (audit)
//    @LastModifiedBy
//    protected String updatedBy;

    // for optimistic locking failure detection
//    @Version
//    private long version;
    public boolean deleted = false;

    public BaseEntity delete() {
        this.deleted = true;
        return this;
    }


}
