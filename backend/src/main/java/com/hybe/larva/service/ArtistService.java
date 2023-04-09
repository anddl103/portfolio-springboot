package com.hybe.larva.service;

import com.hybe.larva.dto.artist.*;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.artist.ArtistAggregation;
import com.hybe.larva.entity.artist.ArtistRepoExt;
import com.hybe.larva.exception.ProtectedResourceException;
import com.hybe.larva.exception.ResourceAlreadyInUseException;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.util.CollectionsUtil;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArtistService {

    private final ArtistRepoExt artistRepoExt;
    private final AlbumRepoExt albumRepoExt;
    private final StorageService storageService;
    private final CacheUtil cacheUtil;

    //@Transactional(value = "mongoTransactionManager")
    public ArtistDetailResponse addArtist(ArtistAddRequest request) {
        try {
            if (Boolean.TRUE.equals(request.isDisplay())) {
                int maxSortOrder = artistRepoExt.getMaxSortOrder();
                request.setSortOrder(maxSortOrder);
            } else {
                request.setSortOrder(0);
            }
            Artist artist = request.toEntity();
            memberIdCheck(artist);
            artist = artistRepoExt.insert(artist);
            return getArtistForBackOffice(artist.getId());
        } catch (Exception e) {
            //파일 삭제
            final List<String> targets = Arrays.asList(request.getLogoKey(), request.getThumbnailKey());
            storageService.deleteByKeys(targets);
            throw new ProtectedResourceException("Artist failed to register");
        }
    }

    public Page<ArtistSearchResponse> searchArtist(ArtistSearchRequest request) {
        final Criteria criteria = Criteria.where(Artist.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Artist.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getName() != null) {
            criteria.and(Artist.NAME).regex(request.getName());
        }

        if (request.getDisplay() != null) {
            criteria.and(Artist.DISPLAY).is(request.getDisplay());
        }

        return artistRepoExt.search(criteria, request.getPageable())
                .map(a -> new ArtistSearchResponse(a, cacheUtil));
    }

    public ArtistSearchResponse getArtist(String artistId) {
        Artist artist = artistRepoExt.findById(artistId);
        return new ArtistSearchResponse(artist);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public ArtistDetailResponse updateArtist(String artistId, ArtistUpdateRequest request) {

        try {
            Artist artist = artistRepoExt.findById(artistId);
            if (request.isDisplay()) {
                if (!artist.isDisplay()) {
                    int maxSortOrder = artistRepoExt.getMaxSortOrder();
                    request.setSortOrder(maxSortOrder);
                }
            } else {
                if (artist.isDisplay()) {
                    artistRepoExt.decSortOrder(artist.getSortOrder());
                    request.setSortOrder(0);
                }
            }

            // DB 내용과 다르면 업데이트로 간주하여 삭제 대상에 포함
            final List<String> deleteTargets = new ArrayList<>();

            if (artist.getThumbnailKey() != null && !StringUtils.equals(artist.getThumbnailKey(), request.getThumbnailKey())) {
                deleteTargets.add(artist.getThumbnailKey());
            }
            if (artist.getLogoKey() != null && !StringUtils.equals(artist.getLogoKey(), request.getLogoKey())) {
                deleteTargets.add(artist.getLogoKey());
            }

            artist = artist.update(request);
            memberIdCheck(artist);
            artistRepoExt.save(artist);

            // 기존 파일 삭제처리
            if (!deleteTargets.isEmpty()) {
                // remove logoUrl from s3
                storageService.deleteByKeys(deleteTargets);
            }

            return getArtistForBackOffice(artistId);
        } catch (Exception e) {
            // 파일 삭제
            final List<String> targets = Arrays.asList(request.getLogoKey(), request.getThumbnailKey());
            storageService.deleteByKeys(targets);
            throw new ProtectedResourceException("Artist failed to edit");
        }
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteArtist(String artistId) {
        boolean album = albumRepoExt.findByArtistId(artistId);
        if (album) {
            throw new ResourceAlreadyInUseException("앨범", "아티스트");
        }

        Artist artist = artistRepoExt.findById(artistId);
        artistRepoExt.delete(artist);
        if (artist.isDisplay()) {
            artistRepoExt.decSortOrder(artist.getSortOrder());
        }
    }

    public boolean findByName(String name, String artistId) {
        Artist artist = artistRepoExt.findByName(name, artistId);
        if (artist != null) {
            return true;
        }
        return false;
    }

    public void updateArtistSortOrder(ArtistUpdateSortOrderRequest request) {
        for (ArtistUpdateSortOrder artistUpdateSortOrder : request.getOrderList()) {
            Artist artist = artistRepoExt.findById(artistUpdateSortOrder.getArtistId()).updateSortOrder(artistUpdateSortOrder.getSortOrder());
            artistRepoExt.save(artist);
        }
    }

    public ArtistDetailResponse getArtistForBackOffice(String artistId) {
        ArtistAggregation artist = artistRepoExt.findByDetail(artistId);
        return new ArtistDetailResponse(artist);
    }

    private void memberIdCheck(Artist artist) {
        if (!CollectionUtils.isEmpty(artist.getMembers())) {
            artist.getMembers().forEach(artistMember -> {
                if (artistMember.getId() == null || artistMember.getId().isEmpty()) {
                    artistMember.setId(Artist.generateUniqueCode());
                }
            });
        }
    }
}
