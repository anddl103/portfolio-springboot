package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.dto.storage.UploadResponse;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static com.hybe.larva.enums.UserRole.ROLES.VIEWER;

@Slf4j
@Api(tags = "Storage")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/storage")
@RestController
public class StorageController {

    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "파일 업로드",
            notes = "AWS S3에 파일을 업로드 한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/upload")
    public SingleResult<UploadResponse> upload(
            @RequestPart MultipartFile file,
            @RequestParam(required = false, defaultValue = "") String folder,
            @RequestParam(required = false, defaultValue = "false") Boolean publicRead
    ) {
        UploadResponse data = storageService.upload(file, folder, publicRead);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "임시 다운로드 URL 생성",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성한다."
    )
    @Secured({PRODUCT_MANAGER})
    @GetMapping("/preSignedUrl")
    public ResponseEntity<Object> preSignedUrl(
            @RequestParam String key,
            @RequestParam(required = false, defaultValue = "60") @Min(0) Long expSeconds
    ) throws IOException, InvalidKeySpecException, URISyntaxException {
        PreSignedUrlResponse data = storageService.generatePreSignedUrl(key, expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find preSignedUrl key=" + key);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "파일 삭제",
            notes = "AWS S3에 파일을 삭제 한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/delete")
    public CommonResult delete(
            @RequestBody @Valid List<UploadResponse> files
    ) {
        storageService.delete(files);

        return ResponseHelper.newSuccessResult();
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "파일 업로드",
            notes = "AWS S3에 파일을 업로드 한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/uploadThumbnail")
    public ListResult<UploadResponse> uploadThumbnail(
            @RequestPart MultipartFile file,
            @RequestParam(required = false, defaultValue = "") String folder,
            @RequestParam(required = false, defaultValue = "false") Boolean publicRead
    ) throws IOException {
        List<UploadResponse> listData = new ArrayList<>();
        UploadResponse data = storageService.upload(file, folder, publicRead);
        listData.add(data);

        log.info("======================  thumbnail start  ==========================");
        log.info(" ContentType : " + file.getContentType());
        log.info(" OriginalFilename : " + file.getOriginalFilename());
        log.info(" size : " + file.getSize());



        MultipartFile thumbnail = createThumbnail(file);

        UploadResponse thumbnailData = storageService.upload(thumbnail, folder, true);
        listData.add(thumbnailData);

        log.info(" ContentType : " + thumbnail.getContentType());
        log.info(" OriginalFilename : " + thumbnail.getOriginalFilename());
        log.info(" size : " + thumbnail.getSize());


        return ResponseHelper.newListSuccessResult(listData);
    }

    // Thumbnail 생성
    private MultipartFile createThumbnail(MultipartFile originalFile) throws IOException{
        log.info(" ====================== start createThumbnail ");

        // Thumbnail width height
        int dw = 250, dh = 250;

        String filename = originalFile.getOriginalFilename();
        ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
        BufferedImage thumbImg = null;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());

        // originalFile width height
        int ow = img.getWidth();
        int oh = img.getHeight();

        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow; int nh = (ow * dh) / dw;

        // 계산된 높이가 원본보다 높다면 crop이 안되므로
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if(nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }

        BufferedImage cropImg = Scalr.crop(img, (ow-nw)/2, (oh-nh)/2, nw, nh);

        thumbImg = Scalr.resize(cropImg, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, dw, dh, Scalr.OP_ANTIALIAS);
        ImageIO.write(thumbImg, originalFile.getContentType().split("/")[1] , thumbOutput);

        log.info(" ====================== ByteArrayOutputStream end ");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(thumbOutput.toByteArray());
        MultipartFile multipartFile = new MockMultipartFile("thumbnail_"+filename, "thumbnail_"+filename, originalFile.getContentType(), readAllBytes(byteArrayInputStream)); //.readAllBytes()

        return multipartFile;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
