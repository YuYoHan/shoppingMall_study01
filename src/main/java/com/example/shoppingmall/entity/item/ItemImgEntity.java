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
    private Long itemImgId;
    private String uploadImgPath;
    private String uploadImgName;               // 이미지 파일명
    private String oriImgName;                  // 원본 이미지 파일명
    private String uploadImgUrl;                // 이미지 조회 경로
    private String repImgYn;                    // 대표 이미지 여부 Y면 대표이미지를 보여줌


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Builder
    public ItemImgEntity(Long itemImgId,
                         String uploadImgPath,
                         String uploadImgName,
                         String oriImgName,
                         String uploadImgUrl,
                         ItemEntity item,
                         String repImgYn) {
        this.itemImgId = itemImgId;
        this.uploadImgPath = uploadImgPath;
        this.uploadImgName = uploadImgName;
        this.oriImgName = oriImgName;
        this.uploadImgUrl = uploadImgUrl;
        this.item = item;
        this.repImgYn = repImgYn;
    }



}
