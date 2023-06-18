package com.example.shoppingmall.service;

import com.example.shoppingmall.entity.ItemImg;
import com.example.shoppingmall.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일업로드
        if(!StringUtils.isEmpty(oriImgName)) {
            // 사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일의 이름, 파일의 바이트 배열을 파일 업로드 파라미터로
            // uploadFile 메소드를 호출합니다. 호출 결과 로컬에 저장된 파일의 이름을 imgName 변수에 저장합니다.
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 저장한 상품 이미지를 불러올 경로를 설정합니다.
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 저장
        // oriImgName : 업로드했던 상품 이미지 파일의 원래 이름
        // imgName : 실제 로컬에 저장된 상품 이미지 파일의 이름
        // imgUrl : 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        // 상품 이미지를 수정한 경우 이미지를 업데이트 합니다.
        if(!itemImgFile.isEmpty()) {
            // 상품 이미지 아이디를 이용하여 기존에 저장했던 상품 이미지 엔티티를 조회합니다.
            ItemImg savedImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            // 기존에 등록된 상품 이미지 파일이 있을 경우 해당 파일을 삭제합니다.
            if(!StringUtils.isEmpty(savedImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            // 업데이트한 상품 이미지 파일을 업로드합니다.
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());

            String imgUrl = "/images/item" + imgName;
            // 변경된 상품 이미지 정보를 세팅해줍니다.
            // 여기서 중요한 점은 상품 등록 때처럼 save() 로직을 호출하지 않는다는 것입니다.
            // savedImg는 영속 상태이므로 데이터를 변경하는 것만으로 변경 감지 기능이 동작하여
            // 트랜잭션이 끝날 때 update 쿼리가 실행됩니다. 여기서 중요한 것은 엔티티가 영속 상태여야 한다는 것이다.
            savedImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

}
