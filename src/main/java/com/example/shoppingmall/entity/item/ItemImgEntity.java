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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long itemImgId;
    @Column(name = "upload_img_path")
    private String uploadImgPath;
    @Column(name = "upload_img_name")
    private String uploadImgName;               // 이미지 파일명
    @Column(name = "ori_img_name")
    private String oriImgName;                  // 원본 이미지 파일명
    @Column(name = "upload_img_url")
    private String uploadImgUrl;                // 이미지 조회 경로
    @Column(name = "rep_img_yn")
    private String repImgYn;                    // 대표 이미지 여부 Y면 대표이미지를 보여줌

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
