package com.sharok.esquela.controller;

import com.sharok.esquela.contract.response.UserResponse;
import com.sharok.esquela.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public UserResponse login(@RequestParam String idToken) {
        UserResponse response = userService.getOrCreateUser(idToken);
        return response;
    }
}
