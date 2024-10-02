package com.tradingtrends.auth.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/member/verify")
    Boolean verifyUser(@RequestParam(value = "user_id") Long userId);

    @GetMapping("/member/id/{user_id}")
    UserResponse getUserById(@PathVariable("user_id") Long userId);

    @GetMapping("/member/{username}")
    UserResponse getUserByUsername(@PathVariable("username") String username);
}
