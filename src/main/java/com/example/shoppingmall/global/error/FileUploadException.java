package com.example.shoppingmall.global.error;
/*
 *   writer : 유요한
 *   work :
 *          파일 업로드 예외처리
 *   date : 2023/10/12
 * */
public class FileUploadException extends RuntimeException{
    public FileUploadException(String message) {
        super(message);
    }
}
