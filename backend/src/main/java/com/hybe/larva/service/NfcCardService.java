package com.hybe.larva.service;

import com.hybe.larva.dto.nfc_card.*;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.nfc_card.NfcCardAggregation;
import com.hybe.larva.entity.nfc_card.NfcCardRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NfcCardService {

    private final NfcCardRepoExt nfcCardRepoExt;
    private final AlbumRepoExt albumRepoExt;
    private final CacheUtil cacheUtil;

    public NfcCardDetailResponse addNfcCard(NfcCardAddRequest request) {
        Album album = albumRepoExt.findById(request.getAlbumId());
        NfcCard nfcCard = request.toEntity(album.getArtistId());
        nfcCard = nfcCardRepoExt.insert(nfcCard);
        return getNfcCard(nfcCard.getId());
    }

    public Page<NfcCardSearchResponse> searchNfcCard(NfcCardSearchRequest request) {
        final Criteria criteria = Criteria.where(NfcCard.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(NfcCard.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getAlbumId() != null) {
            criteria.and(NfcCard.ALBUM_ID).is(request.getAlbumId());
        }

        if (request.getKeyword() != null) {
            criteria.and(NfcCard.TAGS).regex(request.getKeyword());
        }

        if (request.getArtistId() != null) {
            criteria.and(NfcCard.ARTIST_ID).is(request.getArtistId());
        }

        return nfcCardRepoExt.search(criteria, request.getPageable())
                .map(n -> new NfcCardSearchResponse(n, cacheUtil));
    }

    public NfcCardDetailResponse getNfcCard(String cardId) {
        NfcCardAggregation nfcCardAggregation = nfcCardRepoExt.findByIdDetail(cardId);
        return new NfcCardDetailResponse(nfcCardAggregation, cacheUtil);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public NfcCardDetailResponse updateNfcCard(String nfcCardId, NfcCardUpdateRequest request) {
        NfcCard nfcCard = nfcCardRepoExt.findById(nfcCardId).update(request);
        nfcCardRepoExt.save(nfcCard);
        return getNfcCard(nfcCardId);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteNfcCard(String nfcCardId) {
//        nfcCardRepoExt.deleteById(nfcCardId);
        NfcCard nfcCard = nfcCardRepoExt.findById(nfcCardId).delete();
        nfcCardRepoExt.save(nfcCard);
    }
}
