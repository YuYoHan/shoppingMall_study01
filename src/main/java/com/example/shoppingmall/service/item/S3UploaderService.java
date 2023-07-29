package com.example.shoppingmall.service.item;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.shoppingmall.dto.item.ItemImgDTO;
import com.example.shoppingmall.entity.item.ItemImgEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploaderService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public List<ItemImgDTO> upload(String fileType, List<MultipartFile> multipartFiles) throws IOException {
        List<ItemImgDTO> s3files = new ArrayList<>();

        String uploadFilePath = fileType + "/" + getFolderName();

        for (MultipartFile multipartFile : multipartFiles) {
            String oriFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(oriFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                // ex) 구분/년/월/일/파일.확장자
                String keyName = uploadFilePath + "/" + uploadFileName;

                // S3에 폴더 및 파일 업로드
                amazonS3.putObject(
                        new PutObjectRequest(bucket, keyName, inputStream, objectMetadata));

                // TODO : 외부에 공개하는 파일인 경우 Public Read 권한을 추가, ACL 확인
                 /*amazonS3Client.putObject(
                    new PutObjectRequest(bucket, s3Key, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));*/

                // S3에 업로드한 폴더 및 파일 URL
                uploadFileUrl = amazonS3.getUrl(bucket, keyName).toString();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Filed upload failed", e);
            }

            s3files.add(
                    ItemImgDTO.builder()
                            .oriImgName(oriFileName)
                            .uploadImgName(uploadFileName)
                            .uploadImgPath(uploadFilePath)
                            .uploadImgUrl(uploadFileUrl)
                            .build());

        }
        return s3files;
    }

    // S3에 업로드된 파일 삭제
    public String deleteFile(String uploadFilePath, String uuidFileName) {
        String result = "success";

        try {
            // ex) 구분/년/월/일/파일.확장자
            String keyName = uploadFilePath + "/" + uuidFileName;
            boolean isObjectExist = amazonS3.doesObjectExist(bucket, keyName);

            if(isObjectExist) {
                amazonS3.deleteObject(bucket, keyName);
            } else {
                result = "file not found";
            }
        } catch (Exception e) {
            log.debug("Delete File failed", e);
        }
        return result;
    }



    // UUID 파일명 반환
    private String getUuidFileName(String oriFileName) {
        String ext = oriFileName.substring(oriFileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    // 년/월/일 폴더명 반환
    private String getFolderName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", "/");
    }
}
