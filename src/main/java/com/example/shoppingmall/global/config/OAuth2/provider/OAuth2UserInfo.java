package com.example.shoppingmall.global.config.OAuth2.provider;

import java.util.Map;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    Map<String, Object> getAttributes();
}
