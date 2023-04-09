package com.hybe.larva.dto.common;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.util.CacheUtil;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AlbumResponse {

    private String id;

    private String title;

    private String thumbnailKey;

    public AlbumResponse(Album album, CacheUtil cacheUtil) {
        if (album != null) {
            this.id = album.getId();
            this.thumbnailKey = album.getThumbnailKey();
            this.title = cacheUtil.getLanguageResponse("ko", album.getTitle());
        }
    }

    public AlbumResponse(String id, String title, String thumbnailKey, CacheUtil cacheUtil) {
        this.id = id;
        this.title = cacheUtil.getLanguageResponse("ko", title);;
        this.thumbnailKey = thumbnailKey;
    }

}
