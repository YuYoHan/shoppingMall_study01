package com.example.shoppingmall.service.comment;

import com.example.shoppingmall.dto.comment.CommentDTO;
import com.example.shoppingmall.entity.comment.CommentEntity;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.comment.CommentRepository;
import com.example.shoppingmall.repository.item.ItemRepository;
import com.example.shoppingmall.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 댓글 작성
    public ResponseEntity<?> save(Long itemId,
                                  CommentDTO commentDTO,
                                  String memberEmail) {
        try {
            MemberEntity findUser = memberRepository.findByUserEmail(memberEmail);
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);

            List<CommentEntity> commentEntityList = new ArrayList<>();
            if(findUser != null) {
                CommentEntity commentEntity = CommentEntity.builder()
                        .comment(commentDTO.getComment())
                        .member(findUser)
                        .item(findItem)
                        .build();
                CommentEntity saveComment = commentRepository.save(commentEntity);
                commentEntityList.add(saveComment);

                findItem = ItemEntity.builder()
                        .itemId(findItem.getItemId())
                        .itemName(findItem.getItemName())
                        .itemSellStatus(findItem.getItemSellStatus())
                        .price(findItem.getPrice())
                        .member(findItem.getMember())
                        .itemDetail(findItem.getItemDetail())
                        .stockNumber(findItem.getStockNumber())
                        .itemImgList(findItem.getItemImgList())
                        .commentEntityList(commentEntityList)
                        .build();
                itemRepository.save(findItem);
                CommentDTO returnComment = CommentDTO.toCommentDTO(saveComment);
                return ResponseEntity.ok().body(returnComment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 삭제
    public String remove(Long itemId,
                         Long commentId,
                         String memberEmail) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);

            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(EntityNotFoundException::new);

            MemberEntity findUser = memberRepository.findByUserEmail(memberEmail);

            boolean equalsEmail =
                    findUser.getUserEmail().equals(findComment.getMember().getUserEmail());

            boolean equalsId = findComment.getItem().getItemId().equals(findItem.getItemId());

            if(equalsEmail && equalsId) {
                commentRepository.deleteById(findComment.getCommentId());
                return "댓글을 삭제했습니다.";
            } else {
                return "일치하지 않습니다.";
            }
        } catch (Exception e) {
            return "댓글을 삭제하는데 실패했습니다.";
        }
    }

    // 댓글 수정
    public ResponseEntity<?> update(Long itemId,
                                    Long commentId,
                                    CommentDTO commentDTO,
                                    String memberEmail) {
        try {
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);

            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(EntityNotFoundException::new);

            MemberEntity findUser = memberRepository.findByUserEmail(memberEmail);

            boolean equalsEmail =
                    findUser.getUserEmail().equals(findComment.getMember().getUserEmail());

            boolean equalsId = findComment.getItem().getItemId().equals(findItem.getItemId());

            List<CommentEntity> commentEntityList = new ArrayList<>();
            if(equalsEmail && equalsId) {
                findComment = CommentEntity.builder()
                        .commentId(findComment.getCommentId())
                        .comment(commentDTO.getComment())
                        .member(findUser)
                        .item(findItem)
                        .build();
                CommentEntity updateComment = commentRepository.save(findComment);
                commentEntityList.add(updateComment);

                findItem = ItemEntity.builder()
                        .itemId(findItem.getItemId())
                        .itemSellStatus(findItem.getItemSellStatus())
                        .itemDetail(findItem.getItemDetail())
                        .stockNumber(findItem.getStockNumber())
                        .itemName(findItem.getItemName())
                        .itemImgList(findItem.getItemImgList())
                        .member(findItem.getMember())
                        .price(findItem.getPrice())
                        .commentEntityList(commentEntityList)
                        .build();
                itemRepository.save(findItem);
                CommentDTO returnComment = CommentDTO.toCommentDTO(updateComment);
                return ResponseEntity.ok().body(returnComment);
            } else {
                return ResponseEntity.badRequest().body("일치하지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 수정하는데 실패했습니다.");
        }
    }
}
