package com.hybe.larva.entity.view_user_card;

import com.hybe.larva.dto.common.CardContents;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(value = "viewUserCards")
public class ViewUserCard  {

    public static final String ID = "id";
    public static final String UID = "uid";
    public static final String CARD_ID = "cardId";
    public static final String COLLECT_COUNT = "collectCount";
    public static final String USER_CARD_ID = "userCardId";
    public static final String USER_ALBUM_ID = "userAlbumId";
    public static final String USER_ARTIST_ID = "userArtistId";
    public static final String SORT_POSITION = "position";
    public static final String FAVORITE = "favorite";


    private String userCardId;

    private String userArtistId;

    private String uid;

    private String userAlbumId;

    private String cardId;

    private int position;

    private List<String> members;

    private CardContents contents;

    private boolean favorite;

    private int collectCount;

    private boolean newFlag;

    private boolean updatedFlag;

    private LocalDateTime createdAt;

//    @Builder
//    public ViewUserCard(String uid, String userArtistId, String userAlbumId, Card card, boolean favorite,
//                    int collectCount, boolean newFlag, boolean updatedFlag) {
//        this.uid = uid;
//        this.userArtistId = userArtistId;
//        this.userAlbumId = userAlbumId;
//        if (card != null) {
////            this.artistId = card.getArtistId();
////            this.albumId = card.getAlbumId();
//            this.cardId = card.getId();
//        }
//        this.favorite = favorite;
//        this.collectCount = collectCount;
//        this.newFlag = newFlag;
//        this.updatedFlag = updatedFlag;
//    }
}
