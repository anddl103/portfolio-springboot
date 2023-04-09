package com.hybe.larva;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.hybe.larva.config.AppInfo;
import com.hybe.larva.entity.language_pack.LanguagePack;
import com.hybe.larva.entity.view_user_card.ViewUserCardRepoExt;
import com.hybe.larva.service.LanguagePackService;
import com.hybe.larva.service.ViewUserCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableCaching
@Component
@AllArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final CacheManager cacheManager;
    private final ResourceLoader resourceLoader;
    private final LanguagePackService languagePackService;
    private final ViewUserCardService viewUserCardService;
    private final AppInfo appInfo;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        log.info("App Runner.run called");

        languagePackService.setCacheJobEnabled(true);
        languagePackService.setLanguagePackAllCacheChange();

        initFirebase();

        viewUserCardService.createViewUserCard();
    }

    public void initFirebase() throws IOException {

        final String resourceLocation = "classpath:".concat(appInfo.getFirebaseConfig());
        final Resource resource = resourceLoader.getResource(resourceLocation);
        final InputStream inputStream = resource.getInputStream();

        final FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
