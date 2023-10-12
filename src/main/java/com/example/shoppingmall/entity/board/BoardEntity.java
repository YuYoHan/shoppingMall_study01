package com.example.shoppingmall.entity.board;

import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "board")
@Table
@ToString
@Getter
@NoArgsConstructor
public class BoardEntity extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long boardId;

    @Column(length = 300, nullable = false)
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("boardImgId asc")
    private List<BoardImgEntity> boardImgDTOList = new ArrayList<>();

    @Builder
    public BoardEntity(Long boardId,
                       String title,
                       String content,
                       MemberEntity member,
                       List<BoardImgEntity> boardImgDTOList) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.member = member;
        this.boardImgDTOList = boardImgDTOList;
    }
}
