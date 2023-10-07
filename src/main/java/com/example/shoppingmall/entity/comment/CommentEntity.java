package com.example.shoppingmall.entity.comment;

import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "comment")
@Table(name = "comment")
@Getter
@NoArgsConstructor
@ToString
public class CommentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Builder
    public CommentEntity(Long commentId, String comment, MemberEntity member, ItemEntity item) {
        this.commentId = commentId;
        this.comment = comment;
        this.member = member;
        this.item = item;
    }
}
