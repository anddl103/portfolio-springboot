package com.hybe.larva.dto.artist;

import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.artist.ArtistMember;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ToString
@Data
@Builder
public class ArtistAddRequest {

    @NotBlank
    private String name;

    private List<ArtistMember> members;

    private String thumbnailKey;

    private String logoKey;

    private int sortOrder;

    private boolean display;

    public Artist toEntity() {
        return Artist.builder()
                .name(name)
                .members(members)
                .sortOrder(sortOrder)
                .display(display)
                .thumbnailKey(thumbnailKey)
                .logoKey(logoKey)
                .build();
    }
}
