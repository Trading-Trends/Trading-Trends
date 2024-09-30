package com.tradingtrends.user.presentation.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String userId;
    private String username;
    private String password;
    private String email;
}
