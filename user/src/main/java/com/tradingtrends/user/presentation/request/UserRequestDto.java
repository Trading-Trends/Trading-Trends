package com.tradingtrends.user.presentation.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String role;
}
