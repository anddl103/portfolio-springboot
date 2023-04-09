package com.hybe.larva.dto.artist;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.artist.ArtistAggregation;
import com.hybe.larva.entity.artist.ArtistMember;
import com.hybe.larva.entity.language_pack.LanguagePack;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class ArtistDetailResponse extends BaseResponse {

    private final String name;

    private final String thumbnailKey;

    private final String logoKey;

    private List<ArtistMemberResponse> members;

    private int sortOrder;

    private boolean display;

    public ArtistDetailResponse(ArtistAggregation artist) {
        super(artist);
        this.name = artist.getName();
//        this.members = artist.getMembers();

        if (artist.getAs() != null) {
            List<ArtistMemberResponse> artistMemberResponses = new ArrayList<>();
            if (artist.getMembers() != null) {
                for (ArtistMember member : artist.getMembers()) {

                    artistMemberResponses.add(ArtistMemberResponse.builder()
                            .id(member.getId())
                            .name(member.getName())
                            .values(artist.getAs().stream().filter(x -> x.getId().equals(member.getName())).findAny().get().getValues())
                            .build());
                }

                this.members = artistMemberResponses;
            }
        }
        this.thumbnailKey = artist.getThumbnailKey();
        this.logoKey = artist.getLogoKey();
        this.sortOrder = artist.getSortOrder();
        this.display = artist.isDisplay();
    }
}
