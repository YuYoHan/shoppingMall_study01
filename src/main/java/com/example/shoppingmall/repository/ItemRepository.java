package com.example.shoppingmall.repository;

import com.example.shoppingmall.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

// QueryDslPredicateExecutor 인터페이스 상속을 추가합니다.
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    List<Item> findByItemNm(String itemNm);

    // 상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드입니다.
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    // 파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드
    List<Item> findByPriceLessThan(Integer price);

    // OrderBy
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    // @Query 어노테이션 안에 JPQL로 작성한 쿼리문을 넣어줍니다.
    // from 뒤에는 엔티티 클래스로 작성한 Item을 지정해주었고, Item으로부터 데이터를 select하겠다는 의미입니다.
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    // 파라미터 @Param 어노테이션을 이용하여 파라미터로 넘어온 값을 JPQL에 들어갈 변수로 지정해줄 수 있습니다.
    // 현재는 itemDetail 변수를 "like % %" 사이에 ":itemDetail"로 값이 들어가도록 작성했습니다.
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);


//    // value 안에 네이티브 쿼리문을 작성하고 "nativeQuery=true"를 작성한다.
//    @Query(value = "select * from Item i where i.itemDetail like %:itemDetail% order by i.price desc", nativeQuery = true)
//    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);


}
