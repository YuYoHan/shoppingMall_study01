package com.example.shoppingmall.service.board;

import com.example.shoppingmall.dto.board.BoardDTO;
import com.example.shoppingmall.dto.board.BoardImgDTO;
import com.example.shoppingmall.entity.board.BoardEntity;
import com.example.shoppingmall.entity.board.BoardImgEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import com.example.shoppingmall.repository.board.BoardImgRepository;
import com.example.shoppingmall.repository.board.BoardRepository;
import com.example.shoppingmall.repository.member.MemberRepository;
import com.example.shoppingmall.service.s3Upload.S3BoardImgUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;
    private final S3BoardImgUploaderService s3BoardImgUploaderService;
    private final BoardImgRepository boardImgRepository;


    // 게시글 작성
    public ResponseEntity<?> createBoard(BoardDTO boardDTO, List<MultipartFile> boardImages, String userEmail) throws IOException {
        MemberEntity findUser = memberRepository.findByUserEmail(userEmail);

        if (findUser != null) {
            // 게시글을 등록
            // 작성자랑 시간은 Auditing 기능으로 자동으로 DB에 들어감
            BoardEntity boardEntity = BoardEntity.builder()
                    .title(boardDTO.getTitle())
                    .content(boardDTO.getContent())
                    .member(findUser)
                    .build();

            // S3에 이미지 넣기
            List<BoardImgDTO> boardImg = s3BoardImgUploaderService.upload("boardImg", boardImages);
            // List형식으로 이미지를 담는 이유는 Board에 List형식으로
            // 이미지와 양방향을 맺고 있기 때문에 List로 넣어줘야 한다.
            List<BoardImgEntity> boardImgEntities = new ArrayList<>();
            List<BoardDTO> savedImg = new ArrayList<>();

            for (int i = 0; i < boardImg.size(); i++) {
                BoardImgDTO uploadImg = boardImg.get(i);

                BoardImgEntity boardImgEntity = BoardImgEntity.builder()
                        .board(boardEntity)
                        .oriImgName(uploadImg.getOriImgName())
                        .uploadImgName(uploadImg.getUploadImgName())
                        .uploadImgUrl(uploadImg.getUploadImgUrl())
                        .uploadImgPath(uploadImg.getUploadImgPath())
                        .repImgYn(i == 0 ? "Y" : "N")
                        .build();
                // List에 넣어준다.
                boardImgEntities.add(boardImgEntity);
                // 이미지를 DB에 저장
                boardImgRepository.save(boardImgEntity);

                boardEntity = BoardEntity.builder()
                        .title(boardDTO.getTitle())
                        .content(boardDTO.getContent())
                        .member(findUser)
                        .boardImgDTOList(boardImgEntities)
                        .build();

                // 게시글 DB에 저장
                BoardEntity save = boardRepository.save(boardEntity);
                savedImg.add(BoardDTO.toBoardDTO(save));
            }
            return ResponseEntity.ok().body(savedImg);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다.");
        }
    }

    // 상세정보
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBoard(Long boardId) throws Exception {
        try {
            BoardEntity boardEntity = boardRepository.findById(boardId)
                    .orElseThrow(EntityNotFoundException::new);

            List<BoardImgEntity> boardImgList = boardEntity.getBoardImgDTOList();
            List<BoardImgDTO> boardImgDTOList = new ArrayList<>();
            for (BoardImgEntity boardImg : boardImgList) {
                BoardImgDTO boardImgDTO = BoardImgDTO.toBoardImgDTO(boardImg);
                boardImgDTOList.add(boardImgDTO);
            }

            BoardDTO boardDTO = BoardDTO.builder()
                    .boardId(boardEntity.getBoardId())
                    .title(boardEntity.getTitle())
                    .content(boardEntity.getContent())
                    .nickName(boardEntity.getMember().getNickName())
                    .regTime(boardEntity.getRegTime())
                    .boardImgDTOList(boardImgDTOList)
                    .build();

            return ResponseEntity.ok().body(boardDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
        }
    }

    // 게시판 수정
    public ResponseEntity<?> update(Long boardId,
                                    BoardDTO boardDTO,
                                    List<MultipartFile> boardFiles,
                                    String userEmail) throws Exception {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(EntityNotFoundException::new);

        MemberEntity findUser = memberRepository.findByUserEmail(userEmail);

        if (findUser.getUserEmail().equals(boardEntity.getMember().getUserEmail())) {
            boardEntity = BoardEntity.builder()
                    .boardId(boardEntity.getBoardId())
                    .title(boardDTO.getTitle())
                    .content(boardDTO.getContent())
                    .member(findUser)
                    .build();

            // 기존의 이미지 가져오기
            List<BoardImgEntity> boardImgList = boardEntity.getBoardImgDTOList();
            // 새로운 이미지 업로드
            List<BoardImgDTO> board = s3BoardImgUploaderService.upload("board", boardFiles);

            if (boardImgList.isEmpty()) {
                for (int i = 0; i < board.size(); i++) {
                    BoardImgDTO boardImgDTO = board.get(i);
                    BoardImgEntity boardImgEntity = BoardImgEntity.builder()
                            .board(boardEntity)
                            .oriImgName(boardImgDTO.getOriImgName())
                            .uploadImgName(boardImgDTO.getUploadImgName())
                            .uploadImgUrl(boardImgDTO.getUploadImgUrl())
                            .uploadImgPath(boardImgDTO.getUploadImgPath())
                            // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅합니다.
                            // 나머지 상품 이미지는 N으로 설정합니다.
                            .repImgYn(i == 0 ? "Y" : "N") // 대표 이미지 여부 지정
                            .build();

                    boardImgRepository.save(boardImgEntity);
                    boardImgList.add(boardImgEntity);
                }
            } else {
                // 비어있지 않는 경우
                for (BoardImgEntity img : boardImgList) {
                    for (int i = 0; i < board.size(); i++) {
                        BoardImgDTO boardImgDTO = board.get(i);
                        BoardImgEntity boardImgEntity = BoardImgEntity.builder()
                                .boardImgId(img.getBoardImgId())
                                .board(boardEntity)
                                .oriImgName(boardImgDTO.getOriImgName())
                                .uploadImgName(boardImgDTO.getUploadImgName())
                                .uploadImgUrl(boardImgDTO.getUploadImgUrl())
                                .uploadImgPath(boardImgDTO.getUploadImgPath())
                                // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅합니다.
                                // 나머지 상품 이미지는 N으로 설정합니다.
                                .repImgYn(i == 0 ? "Y" : "N") // 대표 이미지 여부 지정
                                .build();

                        boardImgRepository.save(boardImgEntity);
                        boardImgList.add(boardImgEntity);
                    }
                }
            }
            boardEntity = BoardEntity.builder()
                    .boardId(boardEntity.getBoardId())
                    .title(boardDTO.getTitle())
                    .content(boardDTO.getContent())
                    .member(findUser)
                    .boardImgDTOList(boardImgList)
                    .build();

            BoardEntity save = boardRepository.save(boardEntity);
            BoardDTO boardDTO1 = BoardDTO.toBoardDTO(save);
            return ResponseEntity.ok().body(boardDTO1);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 삭제
    public String removeBoard(Long boardId, String userEmail) {
        BoardEntity findBoard = boardRepository.findById(boardId)
                .orElseThrow(EntityNotFoundException::new);

        List<BoardImgEntity> findBoardImg = boardImgRepository.findByBoardBoardId(boardId);

        MemberEntity findUser = memberRepository.findByUserEmail(userEmail);

        if (findUser.getUserEmail().equals(findBoard.getMember().getUserEmail())) {
            for (BoardImgEntity img : findBoardImg) {
                String uploadFilePath = img.getUploadImgPath();
                String uploadFileName = img.getUploadImgName();

                boardRepository.deleteByBoardId(findBoard.getBoardId());
                boardImgRepository.deleteById(img.getBoardImgId());
                String result = s3BoardImgUploaderService.deleteFile(uploadFilePath, uploadFileName);
                log.info(result);
            }
        } else {
            return "해당 유저의 게시글이 아닙니다.";
        }
        return "게시글과 이미지를 삭제했습니다.";
    }

    // 전체 상품 보여주기
    @Transactional(readOnly = true)
    public Page<BoardDTO> getBoards(Pageable pageable) {
        Page<BoardEntity> all = boardRepository.findAll(pageable);
        return all.map(BoardDTO::toBoardDTO);
    }

    // 게시판 검색
    @Transactional(readOnly = true)
    public Page<BoardDTO> getSearchBoards(Pageable pageable, String seachKeyword) {
        Page<BoardEntity> seachBoards = boardRepository.findByTitleContaining(pageable, seachKeyword);
        return seachBoards.map(BoardDTO::toBoardDTO);
    }
}
