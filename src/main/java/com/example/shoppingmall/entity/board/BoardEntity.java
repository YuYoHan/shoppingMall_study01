package com.example.shoppingmall.entity.board;

import com.example.shoppingmall.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "board")
@Table
@ToString
@Getter
@NoArgsConstructor
public class BoardEntity extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @Column(length = 300, nullable = false)
    private String title;
    private String content;

}
