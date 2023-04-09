package com.hybe.larva.dto.banner;

import com.hybe.larva.dto.storage.UploadResponse;
import com.hybe.larva.entity.banner.Banner;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@Builder
public class BannerFileRequest {

    private static final String IMAGE_URL = "imageUrl";

    private Map<String, String> contentsFileMap;

    @Setter
    private List<String> deleteFileListKey;

    public BannerFileRequest fileUpdate(Map<String, UploadResponse> fileMap) {

        fileMap.forEach((key, value) -> {

            String fileUrl = value.getUrl();
            String[] keys = key.split("_");

            this.contentsFileMap.put(keys[0], fileUrl);

        });

        return this;
    }

    public BannerFileRequest fileUpdate(Map<String, UploadResponse> fileMap, Banner banner) {

        fileMap.forEach((key, value) -> {

            String fileUrl = value.getUrl();
            String[] keys = key.split("_");

            this.contentsFileMap.put(keys[0], fileUrl);
            if (banner.getContents().containsKey(keys[0])) {
                deleteFileListKey.add(banner.getContents().get(keys[0]).getImage());
            }
        });

        return this;
    }
}
