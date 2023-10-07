package com.example.shoppingmall.service.s3Upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.shoppingmall.dto.item.ItemImgDTO;
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
public class S3ItemImgUploaderService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    // String fileType는 파일 업로드 시 업로드할 파일들을
    // 어떤 종류 또는 구분으로 분류하고 저장할지를 지정하는 매개변수입니다.
    // 쇼핑몰 프로젝트에서는 보통 상품 이미지들을 업로드하게 되는데,
    // fileType을 사용하여 해당 상품 이미지들을 어떤 카테고리 또는 폴더에 저장할지를 결정할 수 있습니다.
    // 예를 들어, fileType이 "product"인 경우,
    // 상품 이미지들은 "product/년/월/일"과 같은 경로에 업로드될 수 있습니다.
    // 이렇게 파일을 업로드할 경로를 fileType을 기반으로 동적으로
    // 결정하는 것은 이미지 관리 및 구분에 도움이 되며, 폴더를 체계적으로 구성하여 관리할 수 있습니다.
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

            if (isObjectExist) {
                amazonS3.deleteObject(bucket, keyName);
            } else {
                result = "file not found";
            }
        } catch (AmazonS3Exception e) {
            // S3에서 파일 삭제 실패
            result = "S3 file deletion failed: " + e.getMessage();
            log.error("S3 file deletion failed", e);
        } catch (Exception e) {
            // 기타 예외 처리
            result = "file deletion failed: " + e.getMessage();
            log.error("File deletion failed", e);
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
