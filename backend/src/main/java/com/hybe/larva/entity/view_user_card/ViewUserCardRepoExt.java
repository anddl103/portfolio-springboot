package com.hybe.larva.entity.view_user_card;


import com.hybe.larva.entity.album.Album;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ViewUserCardRepoExt {

    private final MongoOperations mongo;
    private final ViewUserCardRepo repo;


    public void viewCreate() {

        mongo.executeCommand("{ \n" +
                "  create: \"viewUserCards\", \n" +
                "  viewOn: \"userAlbums\", \n" +
                "  pipeline: [\n" +
                "    { \n" +
                "        $lookup: { \n" +
                "            from: \"albums\", \n" +
                "            let : {\n" +
                "                objectAlbumId : {\n" +
                "                    \"$toObjectId\" : \"$albumId\"                \n" +
                "                }\n" +
                "            },\n" +
                "            pipeline: [{\n" +
                "                $match: { $expr: { $and: [ {$eq: [\"$_id\", \"$$objectAlbumId\"]} ] } }\n" +
                "    \n" +
                "            }],\n" +
                "            as: \"albums\"\n" +
                "        } \n" +
                "    },\n" +
                "    {\n" +
                "        $addFields: {\n" +
                "            \"albums\" : {\n" +
                "                \"$arrayElemAt\": [ \"$albums\", 0 ]\n" +
                "            },\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        $addFields: {                      \n" +
                "            \"uIds\" : {\n" +
                "                $map: {\n" +
                "                    input: \"$cards\",\n" +
                "                    as: \"r\",\n" +
                "                    in:  \"$$r.cardId\" \n" +
                "                }\n" +
                "            },\n" +
                "            \"aIds\" : {\n" +
                "                $map: {\n" +
                "                    input: \"$albums.cards\",\n" +
                "                    as: \"r\",\n" +
                "                    in: \"$$r._id\"\n" +
                "                }\n" +
                "            },\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        $addFields: {                      \n" +
                "            \"newCards\": {\n" +
                "                $map: {\n" +
                "                    input: \"$aIds\",\n" +
                "                    as: \"r\",\n" +
                "                    in: {\n" +
                "                           id : \"$$r\",\n" +
                "                        index : {\n" +
                "                            $indexOfArray: [ \"$uIds\", \"$$r\" ]\n" +
                "                        },\n" +
                "                        albumCard: {\n" +
                "                            \"$arrayElemAt\": [ \"$albums.cards\", {$indexOfArray: [ \"$aIds\", \"$$r\" ]}  ] \n" +
                "                        },\n" +
                "                        userAlbumCard: {\n" +
                "                            \"$arrayElemAt\": [ \"$cards\", {\n" +
                "                                    $cond: { \n" +
                "                                        if: { $gte: [ {$indexOfArray: [ \"$uIds\", \"$$r\" ]}, 0 ] }, \n" +
                "                                        then: {$indexOfArray: [ \"$uIds\", \"$$r\" ]}, \n" +
                "                                        else:  \"$$REMOVE\"\n" +
                "                                    }\n" +
                "                                }]  \n" +
                "                        },\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        $unwind: \"$newCards\"\n" +
                "    },\n" +
                "    { \n" +
                "        $project: {\n" +
                "            \"userCardId\" : { $ifNull : [{ $toString :  \"$newCards.userAlbumCard._id\" } , \"$$REMOVE\"]},\n" +
                "            \"uid\" : 1,\n" +
                "            \"userArtistId\" : 1,\n" +
                "            \"userAlbumId\" : { $toString : \"$_id\" },\n" +
                "            \"cardId\" : { $toString : \"$newCards.albumCard._id\" },\n" +
                "            \"position\" : \"$newCards.albumCard.position\",  \n" +
                "            \"members\" : \"$newCards.albumCard.members\",\n" +
                "            \"contents\" : \"$newCards.albumCard.contents\",\n" +
                "            \"favorite\" : \"$newCards.userAlbumCard.favorite\",\n" +
                "            \"collectCount\" : \"$newCards.userAlbumCard.collectCount\",\n" +
                "            \"newFlag\" : \"$newCards.userAlbumCard.newFlag\",\n" +
                "            \"updatedFlag\" : \"$newCards.userAlbumCard.updatedFlag\"\n" +
                "            \"createdAt\" : { " +
                "               $ifNull: [ \"$newCards.userAlbumCard.createdAt\", \"$newCards.albumCard.createdAt\" ] " +
                "           }, \n" +
                "        }\n" +
                "    },\n" +
                "    ]\n" +
                "}");

    }

    public List<ViewUserCard> findAll(){
        final Criteria criteria = Criteria.where(Album.DELETED).ne(true);
        Query query = new Query(criteria);
        List<ViewUserCard> viewUserCards = mongo.find(query, ViewUserCard.class);
        return viewUserCards;
    }

    public ViewUserCard findByCardId(String id, String uid) {
        return repo.findByCardIdAndUid(id, uid);
    }

//    public ViewUserCard findByIdDetail(String id, int collectCount) {
//        final Criteria criteria = Criteria.where(ViewUserCard.ID).is(id);
//        criteria.and(ViewUserCard.UID).is(CommonUser.getUid());
//        criteria.and(ViewUserCard.COLLECT_COUNT).gte(collectCount);
//        Query query = new Query(criteria);
//        return mongo.findOne(query, ViewUserCard.class);
//    }

    public ViewUserCard findByIdDetail(Criteria criteria) {

        Query query = new Query(criteria);
        return mongo.findOne(query, ViewUserCard.class);
    }


    public Page<ViewUserCard> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<ViewUserCard> viewUserCards = mongo.find(query, ViewUserCard.class);
        return PageableExecutionUtils.getPage(viewUserCards, pageable,
                () -> mongo.count(new Query(criteria), ViewUserCard.class)
        );
    }
}
