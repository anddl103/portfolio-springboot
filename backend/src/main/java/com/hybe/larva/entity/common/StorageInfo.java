package com.hybe.larva.entity.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor
public class StorageInfo {

    @NotBlank
    private String key;

    @NotBlank
    private String url;

    @NotNull
    private boolean publicRead;

    @Builder
    public StorageInfo(String key, String url, boolean publicRead) {
        this.key = key;
        this.url = url;
        this.publicRead = publicRead;
    }
}
