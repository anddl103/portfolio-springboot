package com.hybe.larva.entity.artist;

import com.hybe.larva.dto.artist.ArtistUpdateRequest;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "artists")
public class Artist extends BaseEntity {

    public static final String NAME = "name";
    public static final String SORT_ORDER = "sortOrder";
    public static final String DISPLAY = "display";

    @Indexed
    private String name;

    private String thumbnailKey;

    private String logoKey;

    private List<ArtistMember> members;

    // migration후 삭제
    private List<String> oldMembers;    

    private int sortOrder;

    private boolean display;

    @Builder
    public Artist(String name, String thumbnailKey, String logoKey, List<ArtistMember> members, int sortOrder,
                  boolean display) {
        this.name = name;
        this.thumbnailKey = thumbnailKey;
        this.logoKey = logoKey;
        this.members = members;
        this.sortOrder = sortOrder;
        this.display = display;
    }

    public static String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public Artist update(ArtistUpdateRequest request) {
        Optional.ofNullable(request.getName()).ifPresent(v -> this.name = v);
        Optional.ofNullable(request.getMembers()).ifPresent(v -> this.members = v);
        Optional.ofNullable(request.getSortOrder()).ifPresent(v -> this.sortOrder = v);
        Optional.ofNullable(request.isDisplay()).ifPresent(v -> this.display = v);
        Optional.ofNullable(request.getThumbnailKey()).ifPresent(v -> this.thumbnailKey = v);
        Optional.ofNullable(request.getLogoKey()).ifPresent(v -> this.logoKey = v);

        return this;
    }

    public Artist delete() {
        this.deleted = true;
        return this;
    }

    public Artist updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

}
