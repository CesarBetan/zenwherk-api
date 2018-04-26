package com.zenwherk.api.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;

@Service
public class AmazonClient {

    private static final Logger logger = LoggerFactory.getLogger(AmazonClient.class);

    private AmazonS3 s3Client;

    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @PostConstruct
    private void initAmazon() {
        this.endpointUrl = "https://s3.us-east-1.amazonaws.com";
        this.accessKey = "AKIAJO6XBHMJS2K4EKFA";
        this.secretKey = "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";
        this.bucketName = "zenwherk-picture-bucket";

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public Optional<String> uploadFile(File file, String fileName) {
        try {
            String fileUrl = "";
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileToS3bucket(fileName, file);
            file.delete();
            return Optional.of(fileUrl);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void uploadFileToS3bucket(String fileName, File file) {
        s3Client.putObject(
                new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
