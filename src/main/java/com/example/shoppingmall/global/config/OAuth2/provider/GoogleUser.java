package com.example.shoppingmall.global.config.OAuth2.provider;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUser extends OAuth2ProviderUser{
    public GoogleUser(OAuth2User oAuth2User,
                      ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getProviderId() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getName() {
        return (String) getAttributes().get("name");
    }
}
