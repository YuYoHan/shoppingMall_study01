package com.example.shoppingmall.domain.board.repository;

import com.example.shoppingmall.domain.board.entity.BoardEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.example.shoppingmall.domain.board.entity.QBoardEntity.boardEntity;
import static com.example.shoppingmall.domain.member.entity.QMemberEntity.memberEntity;
import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BoardEntity> findByBoardId(Long boardId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(boardEntity)
                .join(boardEntity.member, memberEntity).fetchJoin()
                .where(boardEntity.boardId.eq(boardId))
                .fetchOne());
    }

    @Override
    public Page<BoardEntity> findByTitle(Pageable pageable, String searchKeyword) {
        List<BoardEntity> query = queryFactory
                .selectFrom(boardEntity)
                .join(boardEntity.member, memberEntity).fetchJoin()
                .where(search(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(boardEntity.boardId.desc())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(boardEntity.count())
                .from(boardEntity)
                .where(search(searchKeyword));

        return PageableExecutionUtils.getPage(query, pageable, count::fetchOne);
    }

    private BooleanExpression search(String searchKeyword) {
        // likeIgnoreCase는 QueryDSL에서 문자열에 대한 대소문자를 무시하고 부분 일치 검색을 수행하는 메서드입니다.
        // 이 메서드는 SQL에서의 LIKE 연산과 유사하지만, 대소문자를 구분하지 않고 비교합니다.
        return hasText(searchKeyword) ? boardEntity.title.likeIgnoreCase("%"+searchKeyword+"%") : null;
    }

    @Override
    public Page<BoardEntity> findAll(Pageable pageable) {
        List<BoardEntity> query = queryFactory
                .selectFrom(boardEntity)
                .join(boardEntity.member, memberEntity).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(boardEntity.boardId.desc())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(boardEntity.count())
                .from(boardEntity);

        return PageableExecutionUtils.getPage(query, pageable, count::fetchOne);
    }

    @Override
    public Page<BoardEntity> findBoardByEmail(String email,
                                              Pageable pageable,
                                              String searchKeyword) {
        JPAQuery<BoardEntity> query = queryFactory
                .selectFrom(boardEntity)
                .join(boardEntity.member, memberEntity).fetchJoin()
                .where(boardEntity.member.email.eq(email),
                        search(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        sort(pageable, query);
        List<BoardEntity> result = query.fetch();

        JPAQuery<Long> count = queryFactory
                .select(boardEntity.count())
                .from(boardEntity)
                .where(boardEntity.member.email.eq(email),
                        search(searchKeyword));

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private static void sort(Pageable pageable, JPAQuery<BoardEntity> query) {
        for (Order order : pageable.getSort()) {
            // PathBuilder는 주어진 엔터티의 동적인 경로를 생성하는 데 사용됩니다.
            PathBuilder pathBuilder = new PathBuilder(
                    // 엔티티의 타입 정보를 얻어온다.
                    boardEntity.getType(),
                    // 엔티티의 메타데이터를 얻어온다.
                    boardEntity.getMetadata()
            );

            // Order 객체에서 정의된 속성에 해당하는 동적 경로를 얻어오게 됩니다.
            // 예를 들어, 만약 order.getProperty()가 "userName"이라면,
            // pathBuilder.get("userName")는 엔터티의 "userName" 속성에 대한 동적 경로를 반환하게 됩니다.
            // 이 동적 경로는 QueryDSL에서 사용되어 정렬 조건을 만들 때 활용됩니다.
            PathBuilder sort = pathBuilder.get(order.getProperty());

            query.orderBy(
                    new OrderSpecifier<>(
                            order.isDescending() ? DESC : ASC,
                            sort != null ? sort : boardEntity.boardId
                    ));
        }
    }

    @Override
    public Page<BoardEntity> findBoardByNickName(String nickName,
                                                 Pageable pageable,
                                                 String searchKeyword) {
        JPAQuery<BoardEntity> query = queryFactory
                .selectFrom(boardEntity)
                .join(boardEntity.member, memberEntity).fetchJoin()
                .where(boardEntity.member.nickName.eq(nickName),
                        search(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        sort(pageable, query);
        List<BoardEntity> result = query.fetch();
        JPAQuery<Long> count = queryFactory
                .select(boardEntity.count())
                .from(boardEntity)
                .where(boardEntity.member.nickName.eq(nickName),
                        search(searchKeyword));
        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

}
