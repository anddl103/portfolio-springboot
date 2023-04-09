package com.hybe.larva.controller;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.hybe.larva.config.AppInfo;
import com.hybe.larva.dto.health.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@Slf4j
@RequiredArgsConstructor
@RestController
public class HealthController {

    private final AppInfo appInfo;


    @GetMapping("/healthcheck")
    public HealthResponse healthcheck() {
        return HealthResponse.builder()
                .status("OK")
                .profile(appInfo.getProfile())
                .version(appInfo.getVersion())
                .build();
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
                .status("OK")
                .profile(appInfo.getProfile())
                .version(appInfo.getVersion())
                .build();
    }

    @GetMapping("/test")
    public HealthResponse test(Locale locale) throws IOException {

        String distributionDomain = "dty4ildx1lf58.cloudfront.net";
        ClassPathResource resource = new ClassPathResource("private_key.der");
        String keyPairId = "K2SYFO30YJ3CCE";

        String s3ObjectKey = "v3/card/f4d1a5d1-f33b-4259-8672-12aa56472882.jpg";

//        PreSignedUrlResponse data = storageService.generatePreSignedUrl(s3ObjectKey, 300);
//        String[] urls = data.getUrl().split(".com/");
//        log.info("urls : " + urls[1]);
//        String signedURL = "https://" + distributionDomain + "/" + urls[1];
        String signedURL = "";
        String signedURL2 = "";

        try {

            String resourcePath = "https://" + distributionDomain + "/" + s3ObjectKey;

            String ipRange = "0.0.0.0/0";

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, 60);
            Date dateLessThan = ServiceUtils.parseIso8601Date(ServiceUtils.formatIso8601Date(cal.getTime()));

            String customPolicyForSignedUrl = CloudFrontUrlSigner.buildCustomPolicyForSignedUrl(
                    resourcePath, dateLessThan, ipRange, null
            );
            log.info("********** custom policy for signed url = " + customPolicyForSignedUrl);

            File privateKeyFile = resource.getFile();

            PrivateKey privateKey = SignerUtils.loadPrivateKey(privateKeyFile);

            log.info("############ privateKey : " + privateKey.toString());

            SignerUtils.Protocol protocol = SignerUtils.Protocol.https;

            signedURL = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(protocol,
                    distributionDomain,
                    privateKeyFile,
                    s3ObjectKey,
                    keyPairId,
                    dateLessThan);


            signedURL2 = CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
                    resourcePath,
                    keyPairId,
                    privateKey,
                    customPolicyForSignedUrl
            );
            log.info("********** signedURLWithCustomPolicy = " + signedURL);

        } catch (Exception e) {
            e.printStackTrace();
        }




        return HealthResponse.builder()
                .status("OK")
                .profile(signedURL)
                .version(signedURL2)
                .build();
    }
//
//    @GetMapping("/schedul1")
//    public HealthResponse schedul1() {
//        schedulerService.register("mySchedulerId");
//        return HealthResponse.builder()
//                .status("OK")
//                .profile(appInfo.getProfile())
//                .version(appInfo.getVersion())
//                .build();
//    }
//
//
//    @GetMapping("/schedul2")
//    public HealthResponse schedul2() {
//        String aaa = "mySchedulerId";
//        schedulerService.remove(aaa);
//        return HealthResponse.builder()
//                .status("OK")
//                .build();
//    }

}
