package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.entity.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "item_img")
@Table
@ToString
@Getter
@NoArgsConstructor
public class ItemImgEntity extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "item_img_id")
    private Long id;
    private String uploadImgPath;
    private String uploadImgName;               // 이미지 파일명
    private String oriImgName;                  // 원본 이미지 파일명
    private String uploadImgUrl;                // 이미지 조회 경로
    private String repImgYn;                    // 대표 이미지 여부 Y면 대표이미지를 보여줌


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    // 원본 이미지 파일명, 업데이트할 이미지 파일명, 이미지 경로 파라미터로 입력 받아서
    // 이미지 정보를 업데이트 하는 메소드입니다.
    public void updateItemImg(String oriImgName,
                              String uploadImgName,
                              String uploadImgUrl,
                              String uploadImgPath,
                              String repImgYn) {
        this.oriImgName = oriImgName;
        this.uploadImgName = uploadImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.uploadImgPath = uploadImgPath;
        this.repImgYn = repImgYn;
    }

    @Builder
    public ItemImgEntity(Long id,
                         String uploadImgPath,
                         String uploadImgName,
                         String oriImgName,
                         String uploadImgUrl,
                         ItemEntity item,
                         String repImgYn) {
        this.id = id;
        this.uploadImgPath = uploadImgPath;
        this.uploadImgName = uploadImgName;
        this.oriImgName = oriImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.item = item;
        this.repImgYn = repImgYn;
    }
}
