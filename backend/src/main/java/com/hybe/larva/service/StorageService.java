package com.hybe.larva.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.RSA;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.hybe.larva.config.AppInfo;
import com.hybe.larva.consts.FileConst;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.dto.storage.UploadResponse;
import com.hybe.larva.exception.S3FileUploadException;
import com.hybe.larva.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {

    private final AppInfo appInfo;
    private final AmazonS3 amazonS3;

    public UploadResponse upload(MultipartFile file, String folder, boolean publicRead) {
        final String bucket = appInfo.getBucket();

        // make key
        String key = "v3/";
        if (!StringUtils.isEmpty(folder)) key += folder + "/";
        key += UUID.randomUUID().toString();
        key = appendFileExt(key, file.getOriginalFilename());

        // file extension
        final String contentType = file.getContentType();
        final long contentLength = file.getSize();

        long partSize = 5 * 1024 * 1024;
        try {
            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<>();

            // Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(contentLength);
            metadata.setContentEncoding(StandardCharsets.UTF_8.name());

            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, key)
                    .withObjectMetadata(metadata);
            if (publicRead) {
                initRequest.withCannedACL(CannedAccessControlList.PublicRead);
            }
            InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);

            // Upload the file parts.
            // partNo must be an integer between 1 and 10000
            long filePosition = 0;
            for (int partNo = 1; filePosition < contentLength; partNo++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));
                log.debug("### partNo={} partSize={} filePosition={} contentLength={}",
                        partNo, partSize, filePosition, contentLength);

                // Create the request to upload a part.
                UploadPartRequest uploadPartRequest = new UploadPartRequest()
                        .withBucketName(bucket)
                        .withKey(key)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(partNo)
                        .withFileOffset(filePosition)
                        .withInputStream(file.getInputStream())
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadPartResult = amazonS3.uploadPart(uploadPartRequest);
                partETags.add(uploadPartResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucket, key,
                    initResponse.getUploadId(), partETags);
            amazonS3.completeMultipartUpload(compRequest);

            // get url
            URL url = amazonS3.getUrl(bucket, key);

            log.debug("file uploaded: key={}, url={}, public={}", key, url, publicRead);
            return new UploadResponse(key, url.toString(), publicRead);
        } catch (IOException | SdkClientException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error("Failed to upload file: error={}", e.getMessage());
            throw new S3FileUploadException("Failed to upload file: " + file.getOriginalFilename());
        }
    }

    public Map<String, UploadResponse> uploads(HttpServletRequest httpServletRequest, String folder) {

        Map<String, UploadResponse> uploadResponseMap = new HashMap<>();

        Map<String, MultipartFile> files = new HashMap<>();
        if (httpServletRequest instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) httpServletRequest;
            files = multiRequest.getFileMap();
        }
        files.remove(FileConst.REQUEST);

        try {

            files.forEach( (key, file) -> {
                //String folder = "v33";
                boolean publicRead = returnPublicRead(key);
                // upload 영역과 같음
                UploadResponse uploadResponse = upload(file, folder, publicRead);
                uploadResponseMap.put(key, uploadResponse);
            });

        } catch (SdkClientException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error("Failed to upload file: error={}", e.getMessage());

            delete(uploadResponseMap);

            throw new S3FileUploadException("Failed to upload file");
        }

        return uploadResponseMap;
    }


    public PreSignedUrlResponse generatePreSignedUrl(String key, long expSeconds)
            throws IOException, InvalidKeySpecException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expAt = now.plusSeconds(expSeconds);
//        return generatePreSignedUrl(key, expAt);
        return generateCloudFrontPreSignedUrl(key, expAt);
    }

    public PreSignedUrlResponse generatePreSignedUrl(String key, LocalDateTime expAt) {
        final String bucket = appInfo.getBucket();

        // generate the pre-signed URL
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(DateTimeUtil.toDate(expAt));
        URL url = amazonS3.generatePresignedUrl(request);
        log.debug("pre-signed url for {}: {}", key, url);
        return new PreSignedUrlResponse(key, url.toString(), expAt);
    }




    public PreSignedUrlResponse generateCloudFrontPreSignedUrl(String key, LocalDateTime expAt)
            throws IOException, InvalidKeySpecException {

        ClassPathResource privateKeyResource = new ClassPathResource(appInfo.getCloudFrontKeyPath());

        InputStream is = privateKeyResource.getInputStream();
        PrivateKey privateKey = RSA.privateKeyFromPKCS8(com.amazonaws.util.IOUtils.toByteArray(is));

        SignerUtils.Protocol protocol = SignerUtils.Protocol.https;

        String resourceUrlOrPath = SignerUtils.generateResourcePath(protocol, appInfo.getCloudFrontDomain(), key);

        String signedURL = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(resourceUrlOrPath,
                appInfo.getCloudFrontKeyPairId(),
                privateKey,
                DateTimeUtil.toDate(expAt));

        log.debug("pre-signed url for {}: {}", key, signedURL);
        return new PreSignedUrlResponse(key, signedURL, expAt);
    }

    private String appendFileExt(String key, String orgFileName) {
        if (orgFileName != null) {
            int extIdx = orgFileName.lastIndexOf('.');
            return key + orgFileName.substring(extIdx);
        }
        return key;
    }

    /**
     * 여러 파일 삭제
     * @param keys Array of s3 key
     */
    public void deleteByKeys(List<String> keys) {
        List<String> targets = convertToKey(keys);
        final String bucket = appInfo.getClientBucket();
        try {
            for (String key : targets) {
                amazonS3.deleteObject(bucket, key);
                System.out.println(key + " objects successfully deleted.");
            }
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new S3FileUploadException("Failed to delete file AmazonServiceException");
        }
    }

    /**
     * 여러 파일 삭제
     * @param keys Array of s3 key
     */
    public void albumWorkingContentsDeleteByKeys(List<String> keys) {
        List<String> targets = convertToKey(keys);
        final String bucket = appInfo.getBucket();
        try {
            for (String key : targets) {
                amazonS3.deleteObject(bucket, key);
                System.out.println(key + " objects successfully deleted.");
            }
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new S3FileUploadException("Failed to delete file AmazonServiceException");
        }
    }

    /**
     * 단일 파일 삭제
     * @param key s3 key
     */
    public void deleteByKey(String key) {
        final String bucket = appInfo.getClientBucket();
        try {
            amazonS3.deleteObject(bucket, key);
            System.out.println(key + " object successfully deleted.");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new S3FileUploadException("Failed to delete file AmazonServiceException");
        }
    }

    // 기존 파일 삭제 시 사용
    public void delete(List<UploadResponse> uploadResponseList) {

        final String bucket = appInfo.getClientBucket();
        int index = 0;

        try {
            // Upload three sample objects.
            ArrayList<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();

            for (UploadResponse uploadResponse : uploadResponseList) {
                String keyName = uploadResponse.getKey();
                amazonS3.putObject(bucket, keyName, keyName + " to be deleted.");
                keys.add(new DeleteObjectsRequest.KeyVersion(keyName));
            }
            log.info(keys.size() + " objects successfully created.");

            DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket)
                    .withKeys(keys)
                    .withQuiet(false);
            // Verify that the objects were deleted successfully.
            DeleteObjectsResult delObjRes = amazonS3.deleteObjects(multiObjectDeleteRequest);
            int successfulDeletes = delObjRes.getDeletedObjects().size();
            System.out.println(successfulDeletes + " objects successfully deleted.");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new S3FileUploadException("Failed to delete file AmazonServiceException");
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();

            throw new S3FileUploadException("Failed to delete file SdkClientException");
        }
    }

    // 파일 업로드 시 사용
    public void delete(Map<String, UploadResponse> uploadResponseMap) {

        final String bucket = appInfo.getBucket();
        int index = 0;

        try {
            // Upload three sample objects.
            ArrayList<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();

            uploadResponseMap.forEach((key, uploadResponse) -> {
                String keyName = uploadResponse.getKey();
                amazonS3.putObject(bucket, keyName, keyName + " to be deleted.");
                keys.add(new DeleteObjectsRequest.KeyVersion(keyName));
            });
            log.info(keys.size() + " objects successfully created.");

            DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket)
                    .withKeys(keys)
                    .withQuiet(false);
            // Verify that the objects were deleted successfully.
            DeleteObjectsResult delObjRes = amazonS3.deleteObjects(multiObjectDeleteRequest);
            int successfulDeletes = delObjRes.getDeletedObjects().size();
            System.out.println(successfulDeletes + " objects successfully deleted.");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new S3FileUploadException("Failed to delete file AmazonServiceException");
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();

            throw new S3FileUploadException("Failed to delete file SdkClientException");
        }
    }

    /**
     * 기존 파일 삭제
     * @param deleteTargets 삭제 대상 리스트
     */
    public List<String> convertToKey(List<String> deleteTargets) {

        List<String> deleteFileList = new ArrayList<>();

        deleteTargets.forEach(s -> {
            if (s != null && s.length() > 0) {
                final String key = s.contains(".com/") ? s.substring(s.lastIndexOf(".com/")+5) : s;
                deleteFileList.add(key);
            }
        });
        return deleteFileList;
    }

    /**
     * 기존 파일 삭제
     * @param deleteFileListTarget
     */
    public void deleteExistingFile(List<String> deleteFileListTarget) {

       List<UploadResponse> deleteFileList = new ArrayList<>();

        deleteFileListTarget.forEach(s -> {
            if (s != null && s.length() > 0) {
                final String key = s.contains(".com/") ? s.substring(s.lastIndexOf(".com/")+5) : s;
                UploadResponse uploadResponse = UploadResponse.builder()
                        .key(key)
                        .build();
                deleteFileList.add(uploadResponse);
            }
        });

        if (deleteFileList.size() > 0) {
            delete(deleteFileList);
        }
    }

    private boolean returnPublicRead(String publicReadType) {

        boolean publicRead = true;

        if (publicReadType.contains(FileConst.IMAGE_KEY) || publicReadType.contains(FileConst.VIDEO_KEY)) {
            publicRead = false;
        }

        return publicRead;
    }
}
