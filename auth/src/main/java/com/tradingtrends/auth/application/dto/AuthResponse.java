package com.tradingtrends.auth.application.dto;

import lombok.*;

// 유저 로그인 시 응답 객체 입니다.
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    public static AuthResponse of(String accessToken) {
        return AuthResponse.builder().accessToken(accessToken).build();
    }

    // AccessToken과 RefreshToken을 함께 반환하는 정적 메서드 추가 (필요 시)
    public static AuthResponse of(String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
