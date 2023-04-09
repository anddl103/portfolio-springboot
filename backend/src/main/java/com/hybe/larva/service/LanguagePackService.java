package com.hybe.larva.service;


import com.hybe.larva.dto.language_pack.LanguagePackAddRequest;
import com.hybe.larva.dto.language_pack.LanguagePackResponse;
import com.hybe.larva.dto.language_pack.LanguagePackSearchRequest;
import com.hybe.larva.dto.language_pack.LanguagePackUpdateRequest;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.language_pack.LanguagePack;
import com.hybe.larva.entity.language_pack.LanguagePackRepoExt;
import com.hybe.larva.entity.locale_code.LocaleCodeRepoExt;
import com.hybe.larva.entity.locale_code.LocaleCodes;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.exception.InvalidContentTypeException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.util.CacheUtil;
import com.hybe.larva.util.CsvHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.cache.CacheManager;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class LanguagePackService {

    private final LanguagePackRepoExt languagePackRepoExt;
    private final LocaleCodeRepoExt localeCodeRepoExt;
    private final AlbumRepoExt albumRepoExt;
    private final CacheManager cacheManager;
    private final String CACHE_NAME = "product";

    public LanguagePackResponse addLanguagePack(LanguagePackAddRequest request) {
        LanguagePack languagePack = request.toEntity();
        languagePack = languagePackRepoExt.insert(languagePack);
        setLanguagePackOneCacheChange(languagePack);
        return new LanguagePackResponse(languagePack);
    }

    public Page<LanguagePackResponse> searchLanguagePack(LanguagePackSearchRequest request) {
        final Criteria criteria = Criteria.where(NfcCard.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(LanguagePack.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(LanguagePack.VALUES_KO).regex(request.getKeyword());
        }

        return languagePackRepoExt.search(criteria, request.getPageable())
                .map(LanguagePackResponse::new);
    }

    public LanguagePackResponse getLanguagePack(String languagePackId) {
        LanguagePack languagePack = languagePackRepoExt.findById(languagePackId);
        return new LanguagePackResponse(languagePack);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public LanguagePackResponse updateLanguagePack(String languagePackId, LanguagePackUpdateRequest request) {
        LanguagePack languagePack = languagePackRepoExt.findById(languagePackId).update(request);
        languagePack = languagePackRepoExt.save(languagePack);
        setLanguagePackOneCacheChange(languagePack);
        return new LanguagePackResponse(languagePack);
    }

    public void deleteLanguagePack(String languagePackId) {
        languagePackRepoExt.deleteById(languagePackId);
    }

//    public LanguagePack getOneLanguagePack() {
//        return languagePackRepoExt.fineOneLanguagePack();
//    }

    public List<LanguagePack> getAllLanguagePack() {
        return languagePackRepoExt.findAllLanguagePack();
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void mergeLanguagePack(MultipartFile file) {

        if (!CsvHelper.isCsvType(file)) {
            throw new InvalidContentTypeException("Content is not csv type: " + file.getContentType());
        }

        Map<Integer, String> first = new HashMap<>();
        List<LocaleCodes> localeCodes = localeCodeRepoExt.findAll();
        List<String> codes = new ArrayList<>();

        for (LocaleCodes localeCode : localeCodes) {
            codes.add(localeCode.getCode().toLowerCase(Locale.ROOT));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = new CSVParser(reader, CSVFormat.newFormat('|')
                     .withIgnoreEmptyLines().withTrim())) {
            Iterable<CSVRecord> records = parser.getRecords();
            Boolean firstRecord = true;

            for (CSVRecord record : records) {

                int recordSize = record.size();

                if (firstRecord) {
                    firstRecord = false;
                    // header 에서 국가 코드 구분하기
                    for (int i=0; recordSize > i; i++) {
                        if (i < 1 || codes.contains(record.get(i).toLowerCase(Locale.ROOT))) {
                            first.put(i, record.get(i));
                        } else {
                            throw new ResourceNotFoundException("Cannot find country code = " + record.get(i));
                        }
                    }
                } else {
                    Map<String, String> values = new HashMap<>();

                    for (int i=0; recordSize > i; i++) {
                        if (i > 0) {
                            values.put(first.get(i), record.get(i));
                        } else {
                            continue;
                        }
                    }

                    String id = record.get(0);


                    if (id == null || id.trim().length() == 0) {
                        throw new ResourceNotFoundException("Cannot find id = " + id);
                    } else {
                        LanguagePackUpdateRequest languagePackUpdateRequest = LanguagePackUpdateRequest.builder()
                                .values(values)
                                .build();
                        updateLanguagePack(id, languagePackUpdateRequest);
                    }
                }
            }

            // cache 변경 처리
//            setLanguagePackAllCacheChange();
        } catch (IOException e) {
            throw new RuntimeException("Failed to merge LanguagePack: " + e.getMessage());
        }
    }



    public void getLanguagePackDownload(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".csv";

        response.setHeader(headerKey, headerValue);

        List<LanguagePack> data = languagePackRepoExt.findAllLanguagePack();
        List<LocaleCodes> localeCodes = localeCodeRepoExt.findAll();

        if (data.size() > 0) {

            // 언어 code 값 출력
//            Map<String, String> first = data.get(0).getValues();


            final CsvPreference STANDARD_PIPELINE = new CsvPreference.Builder('"', '|', "\r\n").build();


            ICsvMapWriter csvMapWriter = new CsvMapWriter(response.getWriter(), STANDARD_PIPELINE);
            ArrayList<String> defaultHeader = new ArrayList<>();

            ArrayList<String> tempHeader = new ArrayList<>();

            defaultHeader.add(LanguagePack.ID);
            log.info("localeCodes : " + localeCodes.toString());
            localeCodes.forEach((countryCode -> {
                tempHeader.add(countryCode.getCode());
            }));

            Collections.sort(tempHeader);

            defaultHeader.addAll(tempHeader);

            String[] header = new String[ defaultHeader.size() ];
            defaultHeader.toArray(header);

            List<Map<String, String>> bodys = new ArrayList<>();

            data.forEach((key) -> {
                Map<String, String> tempBody = new HashMap<>();
                for (String code : header) {
                    if (code.equals(LanguagePack.ID)) {
                        tempBody.put(code, key.getId());
                    } else {
                        tempBody.put(code, key.getValues().get(code));
                    }
                }
                bodys.add(tempBody);
            });

            csvMapWriter.writeHeader(header);

            for (Map<String, String> body : bodys) {
                csvMapWriter.write(body, header);
            }

            csvMapWriter.close();
        }
    }

    public void setLanguagePackAllCacheChange() {

        List<LanguagePack> languagePacks = getAllLanguagePack();

        Map<String, Map<String, String>> languagePackMap = new HashMap();

        languagePacks.forEach((obj) -> {
            if (obj.getValues() != null) {
                obj.getValues().forEach((key, val) -> {
                    if (val != null && !val.equals("") && !val.trim().equals("")) {
                        Map<String, String> tmpMap = new HashMap();
                        tmpMap.put(obj.getId(), val);
                        if (languagePackMap.containsKey(key)) {
                            languagePackMap.get(key).put(obj.getId(), val);
                        } else {
                            languagePackMap.put(key, tmpMap);
                        }
                    }
                });
            }
        });

        languagePackMap.forEach((key, val) -> {
            setCache(key, val);
        });

        if (languagePacks.size() > 0) {
            cacheLogger();
        }

    }

    private void setCache(String key, Map<String, String> val) {
        cacheManager.getCache(CACHE_NAME).put(key, val);
    }

    private void cacheLogger() {
        cacheManager.getCacheNames().forEach(name -> {

            Map<String, String> map = (Map<String, String>) cacheManager.getCache(name).get("en").get();

        });
    }

    public void setLanguagePackOneCacheChange(LanguagePack languagePack) {
        languagePack.getValues().forEach((key, val) -> {
            setCacheOneLanguagePack(key, languagePack.getId(), val);
        });
    }

    private void setCacheOneLanguagePack(String key, String id, String val) {
        if (val != null && !val.equals("") && !val.trim().equals("")) {
            if (cacheManager.getCache(CACHE_NAME).get(key) == null) {
                log.info(" country_code is empty : " + key);
                Map<String, String> map = new HashMap<>();
                map.put(id, val);
                setCache(key, map);
            } else {
                Map<String, String> map = (Map<String, String>) cacheManager.getCache(CACHE_NAME).get(key).get();
                map.put(id, val);
            }
        }
    }

    public void setCacheJobEnabled(boolean isJobEnabled) {
        cacheManager.getCache(CACHE_NAME).put(CacheUtil.IS_JOB_ENABLED, isJobEnabled);
    }

    public void deleteLanguagePackMembers(List<String> languagePackIds) {
        for (String languagePackId : languagePackIds) {
//            albumRepoExt.updateCardMembers(languagePackId);
//            languagePackRepoExt.deleteById(languagePackId);
        }
    }
}
