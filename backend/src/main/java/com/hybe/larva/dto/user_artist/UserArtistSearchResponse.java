package com.hybe.larva.dto.user_artist;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.artist.ArtistMember;
import com.hybe.larva.entity.user_artist.UserArtist;
import com.hybe.larva.entity.user_artist.UserArtistAggregation;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class UserArtistSearchResponse extends BaseResponse {

    private String artistId;

    private String name;


    private String thumbnailKey;

    private String logoKey;

    private List<UserArtistMemberResponse> members;

    private int sortOrder;

    private boolean display;

    public UserArtistSearchResponse(UserArtistAggregation userArtist, CacheUtil cacheUtil) {
        super(userArtist);

        if (userArtist.getArtist() != null) {
            this.artistId = userArtist.getArtist().getId();
            this.name = userArtist.getArtist().getName();
            this.thumbnailKey = userArtist.getArtist().getThumbnailKey();
            this.logoKey = userArtist.getArtist().getLogoKey();
            this.members = getMembersLocaleCode(userArtist, cacheUtil);
            this.sortOrder = userArtist.getArtist().getSortOrder();
            this.display = userArtist.getArtist().isDisplay();
        }
    }

    private List<UserArtistMemberResponse> getMembersLocaleCode(UserArtistAggregation userArtist, CacheUtil cacheUtil) {
        List<UserArtistMemberResponse> artistMemberResponses = new ArrayList<>();
        if (userArtist.getArtist().getMembers().size() > 0) {
            for (ArtistMember member : userArtist.getArtist().getMembers()) {
                artistMemberResponses.add(UserArtistMemberResponse.builder()
                        .id(member.getId())
                        .name(cacheUtil.getLanguageResponse(member.getName()))
                        .build());
            }
        }

        return artistMemberResponses;
    }
}
