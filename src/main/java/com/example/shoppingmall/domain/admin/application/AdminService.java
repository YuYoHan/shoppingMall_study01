package com.example.shoppingmall.domain.admin.application;

import com.example.shoppingmall.domain.admin.dto.ResponseAdminDTO;
import com.example.shoppingmall.domain.board.dto.ResponseBoardDTO;
import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.example.shoppingmall.domain.board.repository.BoardRepository;
import com.example.shoppingmall.domain.item.repository.ItemImgRepository;
import com.example.shoppingmall.domain.item.repository.ItemRepository;
import com.example.shoppingmall.domain.member.application.MemberService;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.domain.model.Secret;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AdminService {
    // 상품 관련
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;

    // 유저 관련
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // MailConfig에서 등록해둔 Bean을 autowired하여 사용하기
    private final JavaMailSender emailSender;
    // 사용자가 메일로 받을 인증번호
    private String key;
    @Value("${naver.id}")
    private String id;
    // Instant 클래스는 특정 지점의 시간을 나타내기 위한 클래스입니다.
    // 코드 생성 시간을 나타내는 Instant 객체입니다.
    private Instant codeGenerationTime;
    // Duration 클래스는 두 시간 간의 차이를 나타내기 위한 클래스입니다.
    // Duration.ofMinutes(1)을 사용하여 1분으로 설정합니다.
    private Duration validityDuration = Duration.ofMinutes(1);

    // 게시글 관련
    private final BoardRepository boardRepository;

    // 관리자 회원가입
    public ResponseEntity<?> adminSignUp(RequestMemberDTO admin) {
        try {
            log.info("email : " + admin.getEmail());
            log.info("nickName : " + admin.getNickName());

            // 이메일 중복 체크
            if (!memberService.emailCheck(admin.getEmail())) {
                return ResponseEntity.badRequest().body("이미 존재하는 이메일이 있습니다.");
            }

            // 닉네임 중복 체크
            if (!memberService.nickNameCheck(admin.getNickName())) {
                return ResponseEntity.badRequest().body("이미 존재하는 닉네임이 있습니다.");
            }
            // 비밀번호 암호화
            String encodePw = passwordEncoder.encode(admin.getMemberPw());

            // 아이디가 없다면 DB에 등록해줍니다.
            MemberEntity adminId = MemberEntity.saveMember(admin, encodePw);
            log.info("admin in service : " + adminId);
            MemberEntity saveMember = memberRepository.save(adminId);

            ResponseAdminDTO coverMember = ResponseAdminDTO.toMemberDTO(saveMember);
            return ResponseEntity.ok().body(coverMember);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("회원 가입중 오류가 발생했습니다.");
        }
    }

    // 메일 전송
    // 메일 전송
    public String sendMail(String email) throws Exception {
        // 랜덤 인증 코드 생성
        key = createKey();
        log.info("********생성된 랜덤 인증코드******** => " + key);
        // 메세지 생성
        MimeMessage message = createMessage(email);
        log.info("********생성된 메시지******** => " + message);
        try {
            // 메일로 보냄
            emailSender.send(message);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        // 메일로 사용자에게 보낸 인증코드를 서버로 반환! 인증코드 일치여부를 확인하기 위함
        return key;
    }

    // 랜덤 인증 코드 생성
    private String createKey() throws Exception {
        int length = 6;
        try {
            // SecureRandom.getInstanceStrong()을 호출하여
            // 강력한 (strong) 알고리즘을 사용하는 SecureRandom 인스턴스를 가져옵니다.
            // 이는 예측하기 어려운 안전한 랜덤 값을 제공합니다.
            SecureRandom random = SecureRandom.getInstanceStrong();
            // StringBuilder는 가변적인 문자열을 효율적으로 다루기 위한 클래스입니다.
            // 여기서는 생성된 랜덤 값을 문자열로 변환하여 저장하기 위해 StringBuilder를 사용합니다.
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberService.createCode() exception occur");
            throw new UserException(e.getMessage());
        }
    }

    // 메일 내용 작성
    private MimeMessage createMessage(String email) throws MessagingException {
        log.info("메일받을 사용자 : " + email);
        log.info("인증번호 : " + key);
        codeGenerationTime = Instant.now();
        log.info("********코드 생성 시간******** => " + codeGenerationTime);
        log.info("********유효 시간******** => " + validityDuration.toMinutes());

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        // 이메일 제목
        message.setSubject("관리자 회원가입 인증코드");
        String msgg = "";
        msgg += "<h1>안녕하세요</h1>";
        msgg += "<h1>저희는 BlueBucket 이커머스 플랫폼 입니다</h1>";
        msgg += "<br>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black'>";
        msgg += "<h3 style='color:blue'>회원가입 인증코드 입니다</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "<strong>" + key + "</strong></div><br/>"; // 메일에 인증번호 ePw 넣기
        msgg += "<p>유효 시간: " + validityDuration.toMinutes() + "분 동안만 유효합니다.</p>";
        msgg += "</div>";
        // 메일 내용, charset타입, subtype
        message.setText(msgg, "utf-8", "html");
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(id);
        log.info("********creatMessage 함수에서 생성된 msgg 메시지********" + msgg);
        log.info("********creatMessage 함수에서 생성된 리턴 메시지********" + message);

        return message;
    }

    // 사용자가 입력한 인증번호와 서버에서 생성한 인증번호를 비교하는 메서드
    public String verifyCode(String code) {
        try {
            if (codeGenerationTime == null) {
                // 시간 정보가 없으면 유효하지 않음
                return "시간 정보가 없습니다.";
            }
            // 현재 시간과 코드 생성 시간의 차이 계산
            Duration elapsedDuration = Duration.between(codeGenerationTime, Instant.now());
            // 남은 시간 계산: 전체 유효 기간에서 경과된 시간을 뺀다
            long remainDuration = validityDuration.minus(elapsedDuration).getSeconds();

            // 시간이 0보다 높으면 즉, 유효기간이 지나지 않으면
            // 사용자가 입력한 인증번호와 서버에서 생성한 인증번호를 비교해서 맞다면 true
            if (remainDuration > 0) {
                if (code.equals(key)) {
                    return "인증 번호가 일치합니다.";
                }
            } else if (remainDuration < 0) {
                return "인증 번호의 유효시간이 지났습니다.";
            } else if (!code.equals(key)) {
                return "인증 번호가 일치하지 않습니다.";
            }
            return null;
        } catch (NullPointerException e) {
            // 사용자가 정수가 아닌 값을 입력한 경우
            return "유효하지 않는 인증 번호를 입력했습니다.";
        }
    }

    @Transactional(readOnly = true)
    public Page<ResponseBoardDTO> getAllBoards(Pageable pageable,
                                       String searchKeyword,
                                       UserDetails userDetails) {
        // userDetails에서 권한을 가져오기
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        String email = userDetails.getUsername();
        MemberEntity findEmail = memberRepository.findByEmail(email);
        log.info("관리자 정보 : " + findEmail);

        Page<BoardEntity> allBoards;
        // 권한이 있는지 체크
        if (!authorities.isEmpty()) {
            String role = authorities.iterator().next().getAuthority();
            log.info("권한 : " + role);
            // 관리자 권한 체크
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                // 키워드로 페이지 처리해서 검색
                allBoards = boardRepository.findByTitle(pageable, searchKeyword);
                // 댓글이 존재하는지 아닌지 체크할 수 있게 상태를 바꿔줍니다.
                allBoards.forEach(BoardEntity::replyCheck);

                // 관리자라 모두 읽을 수 있으니 UN_LOCK
                allBoards.forEach(board -> board.changeSecret(Secret.UN_LOCK));
                return allBoards.map(ResponseBoardDTO::changeDTO);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Page<ResponseBoardDTO> getBoardsByNiickName(UserDetails userDetails,
                                               Pageable pageable,
                                               String nickName,
                                               String searchKeyword) {
        // userDetails에서 권한을 가져오기
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // 현재는 권한이 1개만 있는 것으로 가정
        if (!authorities.isEmpty()) {
            String role = authorities.iterator().next().getAuthority();
            // 존재하는 권한이 관리자인지 체크
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                Page<BoardEntity> allByNickName = boardRepository.findBoardByNickName(nickName, pageable, searchKeyword);
                // 댓글이 존재하는지 아닌지 체크할 수 있게 상태를 바꿔줍니다.
                allByNickName.forEach(BoardEntity::replyCheck);
                // 관리자라 모두 읽을 수 있으니 UN_LOCK
                allByNickName.forEach(board -> board.changeSecret(Secret.UN_LOCK));
                return allByNickName.map(ResponseBoardDTO::changeDTO);
            }
        }
        return null;
    }

    // 문의글 상세 보기
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseBoardDTO> getBoard(Long boardId, UserDetails userDetails) {
        try {
            // userDetails에서 권한을 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // 문의글 조회
            BoardEntity findBoard = boardRepository.findByBoardId(boardId)
                    .orElseThrow(EntityNotFoundException::new);

            // 권한이 있는지 체크
            if (!authorities.isEmpty()) {
                String role = authorities.iterator().next().getAuthority();
                log.info("권한 : " + role);
                if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                    ResponseBoardDTO returnBoard = ResponseBoardDTO.changeDTO(findBoard);
                    return ResponseEntity.ok().body(returnBoard);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
