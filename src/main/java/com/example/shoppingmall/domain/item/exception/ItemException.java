package com.example.shoppingmall.domain.item.exception;
/*
 *   writer : 유요한
 *   work :
 *          상품 예외처리
 *   date : 2023/10/12
 * */
public class ItemException extends RuntimeException{
    public ItemException(String message) {
        super(message);
    }
}
