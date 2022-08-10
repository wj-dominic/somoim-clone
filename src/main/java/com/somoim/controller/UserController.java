package com.somoim.controller;

import com.somoim.annotation.LoginCheck;
import com.somoim.model.dto.LoginUser;
import com.somoim.model.dto.SignUpUser;
import com.somoim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public void createUser(@Valid @RequestBody SignUpUser user) {
        userService.createUser(user);
    }

    @LoginCheck
    @PostMapping("/resign")
    public void deleteUser() {
        userService.deleteUser();
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginUser loginUser) {
        userService.loginUser(loginUser);
        return ResponseEntity.ok("login success");
    }

    @LoginCheck
    @GetMapping("/logout")
    public void logoutUser() {
        userService.logoutUser();
    }
}
