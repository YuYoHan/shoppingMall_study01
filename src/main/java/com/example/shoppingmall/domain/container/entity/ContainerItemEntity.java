package com.example.shoppingmall.domain.container.entity;

import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity(name = "container")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "item")
@Builder
@AllArgsConstructor
public class ContainerItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    private Long id;
    @Column(name = "container_name")
    private String containerName;
    @Column(name = "container_addr")
    private String containerAddr;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public static ContainerItemEntity saveContainer(ItemEntity item) {
        return ContainerItemEntity.builder()
                .containerName(item.getItemPlace().getContainerName())
                .containerAddr(item.getItemPlace().getContainerAddr())
                .item(item)
                .build();
    }
}
