package com.hybe.larva.entity.artist;

import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.language_pack.LanguagePack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistAggregation extends BaseEntity {

    private String name;

    private String thumbnailKey;

    private String logoKey;

    private List<ArtistMember> members;

    private int sortOrder;

    private boolean display;

    private List<LanguagePack> as;
}
