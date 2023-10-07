package com.example.shoppingmall.service.admin;

import com.example.shoppingmall.entity.board.BoardEntity;
import com.example.shoppingmall.entity.board.BoardImgEntity;
import com.example.shoppingmall.entity.item.ItemEntity;
import com.example.shoppingmall.entity.item.ItemImgEntity;
import com.example.shoppingmall.repository.board.BoardImgRepository;
import com.example.shoppingmall.repository.board.BoardRepository;
import com.example.shoppingmall.repository.comment.CommentRepository;
import com.example.shoppingmall.repository.item.ItemImgRepository;
import com.example.shoppingmall.repository.item.ItemRepository;
import com.example.shoppingmall.service.s3Upload.S3BoardImgUploaderService;
import com.example.shoppingmall.service.s3Upload.S3ItemImgUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdminService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final S3ItemImgUploaderService s3ItemImgUploaderService;
    private final BoardRepository boardRepository;
    private final BoardImgRepository boardImgRepository;
    private final S3BoardImgUploaderService s3BoardImgUploaderService;
    private final CommentRepository commentRepository;

    // 상품 삭제
    public String removeItem(Long itemId, UserDetails userDetails){
        try {
            // 권한이 있는지 확인
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> collect = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 상품 조회
            ItemEntity findItem = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);
            log.info("item : " + findItem);

            // 이미지 조회
            List<ItemImgEntity> findImg = itemImgRepository.findByItemItemId(itemId);

            for (String role : collect) {
                if (role.equals("ROLE_ADMIN")) {
                    for (ItemImgEntity img : findImg) {
                        String uploadFilePath = img.getUploadImgPath();
                        String uuidFileName = img.getUploadImgName();

                        // 상품 정보 삭제
                        itemRepository.deleteByItemId(itemId);
                        // 이미지 삭제
                        itemImgRepository.deleteById(img.getItemImgId());
                        // S3에서 이미지 삭제
                        s3ItemImgUploaderService.deleteFile(uploadFilePath, uuidFileName);
                        // 댓글 삭제
                        commentRepository.deleteByItemItemId(findItem.getItemId());
                    }
                } else {
                    return "삭제할 권한이 없습니다.";
                }
            }
            return "게시물을 삭제했습니다.";
        } catch (EntityNotFoundException e) {
            return "게시물이 없습니다.";
        }
    }

    // 게시글 삭제
    public String removeBoard(Long boardId, UserDetails userDetails) throws Exception {
        try {
            Collection<? extends GrantedAuthority> authorities =
                    userDetails.getAuthorities();
            List<String> collect = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            BoardEntity findBoard = boardRepository.findById(boardId)
                    .orElseThrow(EntityNotFoundException::new);
            log.info("board : " + findBoard);

            List<BoardImgEntity> findImg = boardImgRepository.findByBoardBoardId(boardId);
            log.info("img : " + findImg);

            for(String role : collect) {
                if(role.equals("ROLE_ADMIN")) {
                    for(BoardImgEntity img : findImg) {
                        String uploadImgPath = img.getUploadImgPath();
                        String uploadImgName = img.getUploadImgName();

                        boardRepository.deleteByBoardId(boardId);
                        boardImgRepository.deleteById(img.getBoardImgId());
                        s3BoardImgUploaderService.deleteFile(uploadImgPath,uploadImgName);
                    }
                } else {
                    return "삭제할 권한이 없습니다.";
                }
            }
            return "게시물을 삭제했습니다.";
        } catch (EntityNotFoundException e) {
            return "게시물이 없습니다.";
        }
    }
}
