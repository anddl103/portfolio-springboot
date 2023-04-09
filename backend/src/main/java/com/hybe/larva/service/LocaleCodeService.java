package com.hybe.larva.service;

import com.hybe.larva.dto.locale_code.LocaleCodeAddRequest;
import com.hybe.larva.dto.locale_code.LocaleCodeResponse;
import com.hybe.larva.dto.locale_code.LocaleCodeSearchRequest;
import com.hybe.larva.dto.locale_code.LocaleCodeUpdateRequest;
import com.hybe.larva.entity.locale_code.LocaleCodeRepoExt;
import com.hybe.larva.entity.locale_code.LocaleCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocaleCodeService {

    private final LocaleCodeRepoExt localeCodeRepoExt;

    public LocaleCodeResponse addLocaleCode(LocaleCodeAddRequest request) {
        LocaleCodes localeCodes = request.toEntity();
        localeCodes = localeCodeRepoExt.insert(localeCodes);
        return new LocaleCodeResponse(localeCodes);
    }

    public Page<LocaleCodeResponse> searchLocaleCode(LocaleCodeSearchRequest request) {
        final Criteria criteria = Criteria.where(LocaleCodes.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(LocaleCodes.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        return localeCodeRepoExt.search(criteria, request.getPageable())
                .map(LocaleCodeResponse::new);
    }

    public LocaleCodeResponse getLocaleCode(String cardId) {
        LocaleCodes localeCodes = localeCodeRepoExt.findById(cardId);
        return new LocaleCodeResponse(localeCodes);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public LocaleCodeResponse updateLocaleCode(String countryCodeId, LocaleCodeUpdateRequest request) {
        LocaleCodes localeCodes = localeCodeRepoExt.findById(countryCodeId).update(request);
        localeCodes = localeCodeRepoExt.save(localeCodes);
        return new LocaleCodeResponse(localeCodes);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteLocaleCode(String countryCodeId) {
        LocaleCodes localeCodes = localeCodeRepoExt.findById(countryCodeId).delete();
        localeCodeRepoExt.save(localeCodes);
    }

    public List<LocaleCodeResponse> getLocaleCodes() {
        return localeCodeRepoExt.findAll().stream()
                .map(LocaleCodeResponse::new).collect(Collectors.toList());
    }
}
