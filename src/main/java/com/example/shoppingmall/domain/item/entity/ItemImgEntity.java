package com.example.shoppingmall.domain.item.entity;

import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"item"})
public class ItemImgEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String filePath;
    // 대표 이미지 여부 Y면 대표이미지를 보여줌
    @Column(name = "rep_img_yn")
    private String repImgYn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public static ItemImgEntity saveImg(String file_path,
                                        List<MultipartFile> itemFiles,
                                        ItemEntity item) {
        String filePath = null;
        String originalFilename = null;
        String type = null;
        if (!itemFiles.isEmpty()) {
            for (MultipartFile itemFile : itemFiles) {
                filePath = file_path + itemFile.getOriginalFilename();
                originalFilename = itemFile.getOriginalFilename();
                type = itemFile.getContentType();
            }
        }
        return ItemImgEntity.builder()
                .name(originalFilename)
                .type(type)
                .filePath(filePath)
                .item(item)
                .build();
    }
        public void changeRepImgY () {
            this.repImgYn = "Y";
        }
        public void changeRepImgN () {
            this.repImgYn = "N";
        }

    }
