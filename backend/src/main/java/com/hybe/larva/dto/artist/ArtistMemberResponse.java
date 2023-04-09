package com.hybe.larva.dto.artist;

import com.hybe.larva.entity.language_pack.LanguagePack;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;


@ToString(callSuper = true)
@Getter
@Builder
public class ArtistMemberResponse {
    private String id;

    private String name;

    private Map<String, String> values;
}
