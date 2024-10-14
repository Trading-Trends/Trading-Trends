package com.tradingtrends.auth.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/member/verify")
    Boolean verifyUser(@RequestParam(value = "user_id") Long userId);

    @GetMapping("/api/member/{user_id}/details")
    UserDetailsDto getUserDetailsById(@PathVariable("user_id") Long userId);

    @GetMapping("/api/member")
    UserDetailsDto getUserDetailsByUsername(@RequestParam(value = "username") String username);
}
