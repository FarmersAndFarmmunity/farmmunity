package com.shop.farmmunity.domain.item.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, String imgName) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, imgName, multipartFile.getInputStream(), metadata);
        } catch (Exception e) {
            log.error("Can not upload image, ", e);
            throw new RuntimeException("cannot upload image");
        }

        return amazonS3.getUrl(bucket, imgName).toString();
    }

    public void deleteFile(String originalFilename) {
        try {
            amazonS3.deleteObject(bucket, originalFilename);
        } catch (Exception e) {
            log.error("Can not delete image, ", e);
            throw new RuntimeException("cannot delete image");
        }
    }
}
