package com.hybe.larva.dto.artist;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.user_artist.UserArtistMemberResponse;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.artist.ArtistAggregation;
import com.hybe.larva.entity.artist.ArtistMember;
import com.hybe.larva.entity.language_pack.LanguagePack;
import com.hybe.larva.entity.user_artist.UserArtist;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true)
@Getter
public class ArtistSearchResponse extends BaseResponse {

    private final String name;

    private final String thumbnailKey;

    private final int sortOrder;

    private final boolean display;

    private final String logoKey;


    private List<UserArtistMemberResponse> members;

    public ArtistSearchResponse(Artist artist) {
        super(artist);
        this.name = artist.getName();
        this.thumbnailKey = artist.getThumbnailKey();
        this.logoKey = artist.getLogoKey();
        this.sortOrder = artist.getSortOrder();
        this.display = artist.isDisplay();
    }

    // test 를 위해 임시로 적용 작업 후 삭제 예정
    public ArtistSearchResponse(Artist artist, CacheUtil cacheUtil) {
        super(artist);
        this.name = artist.getName();
        this.thumbnailKey = artist.getThumbnailKey();
        this.sortOrder = artist.getSortOrder();
        this.display = artist.isDisplay();
        this.logoKey = artist.getLogoKey();
        this.members = getMembersLocaleCode(artist, cacheUtil);
    }

    // test 를 위해 임시로 적용 작업 후 삭제 예정
    private List<UserArtistMemberResponse> getMembersLocaleCode(Artist artist, CacheUtil cacheUtil) {
        List<UserArtistMemberResponse> artistMemberResponses = new ArrayList<>();
        if (artist.getMembers().size() > 0) {
            for (ArtistMember member : artist.getMembers()) {
                artistMemberResponses.add(UserArtistMemberResponse.builder()
                        .id(member.getId())
                        .name(cacheUtil.getLanguageResponse(member.getName()))
                        .build());
            }
        }

        return artistMemberResponses;
    }

}
