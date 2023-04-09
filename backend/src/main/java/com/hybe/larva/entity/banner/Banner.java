package com.hybe.larva.entity.banner;

import com.hybe.larva.dto.banner.BannerFileRequest;
import com.hybe.larva.dto.banner.BannerUpdateRequest;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "banners")
public class Banner extends BaseEntity {

    public static final String TAGS = "tags";
    public static final String DISPLAY = "display";
    public static final String SORT_ORDER = "sortOrder";
    public static final String ALBUM_ID = "albumId";
    public static final String ARTIST_ID = "artistId";

    public static final String AG_ARTIST_ID = "$artistId";
    public static final String AG_ARTIST_FROM_COLLECTION = "artists";
    public static final String AG_ARTIST_ADD_FIELD = "artistObjectId";
    public static final String AG_ARTIST_FOREIGN_FIELD = "_id";
    public static final String AG_ARTIST_AS = "artist";

    private List<String> tags;

    private String title;

    private String albumId;

    private String albumTitle;

    private String thumbnailKey;

    private String artistId;

    private String artistName;

    // link, imageUrl
    private Map<String, BannerContents> contents;

    private int sortOrder;

    private boolean display;

    @Builder
    public Banner(List<String> tags, String title, String albumId, String albumTitle,String thumbnailKey,
                  String artistId, String artistName, Map<String, BannerContents> contents, int sortOrder,
                  boolean display) {
        this.tags = tags;
        this.title = title;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.thumbnailKey = thumbnailKey;
        this.artistId = artistId;
        this.artistName = artistName;
        this.contents = contents;
        this.sortOrder = sortOrder;
        this.display = display;
    }


    public Banner update(final BannerUpdateRequest request, String albumTitle, String artistName) {
        Optional.ofNullable(request.getTags()).ifPresent(v -> this.tags = v);
        Optional.ofNullable(request.getTitle()).ifPresent(v -> this.title = v);
        Optional.ofNullable(request.getThumbnailKey()).ifPresent(v -> this.thumbnailKey = v);
        Optional.ofNullable(request.getAlbumId()).ifPresent(v -> {
            this.albumId = v;
            this.albumTitle = albumTitle;
        });
        Optional.ofNullable(request.getArtistId()).ifPresent(v -> {
            this.artistId = v;
            this.artistName = artistName;
        });
        Optional.ofNullable(request.getContents()).ifPresent(v -> this.contents = v);
        Optional.ofNullable(request.getSortOrder()).ifPresent(v -> this.sortOrder = v);
        Optional.of(request.isDisplay()).ifPresent(v -> this.display = v);

        return this;
    }

    public Banner delete() {
        this.deleted = true;
        this.sortOrder = 0;
        return this;
    }

    public Banner updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

}
