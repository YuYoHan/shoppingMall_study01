package com.example.shoppingmall.domain.board.dto;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.example.shoppingmall.domain.board.entity.ReplyStatus;
import com.example.shoppingmall.domain.comment.dto.CommentDTO;
import com.example.shoppingmall.domain.comment.entity.CommentEntity;
import com.example.shoppingmall.domain.model.Secret;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class ResponseBoardDTO {
    private Long boardId;

    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    private String content;

    private String nickName;

    private LocalDateTime regTime;

    private List<CommentDTO> commentDTOList = new ArrayList<>();

    private Secret boardSecret;

    private ReplyStatus replyStatus;

    @Builder
    public ResponseBoardDTO(Long boardId,
                    String title,
                    String content,
                    String nickName,
                    LocalDateTime regTime,
                    List<CommentDTO> commentDTOList,
                            Secret boardSecret,
                    ReplyStatus replyStatus) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.regTime = regTime;
        this.commentDTOList = commentDTOList;
        this.boardSecret = boardSecret;
        this.replyStatus = replyStatus;
    }

    public static ResponseBoardDTO changeDTO(BoardEntity board) {
        List<CommentEntity> commentEntityList = board.getCommentEntityList();
        List<CommentDTO> collect = commentEntityList.stream()
                .map(CommentDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseBoardDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .nickName(board.getMember().getNickName())
                .commentDTOList(collect)
                // 답글 미완료 상태로 등록
                .replyStatus(board.getReplyStatus())
                .regTime(board.getRegTime())
                .boardSecret(board.getBoardSecret())
                .build();
    }
}
