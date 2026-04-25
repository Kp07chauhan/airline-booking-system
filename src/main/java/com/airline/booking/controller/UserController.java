package com.airline.booking.controller;

import com.airline.booking.dto.user.LoginResponseDto;
import com.airline.booking.dto.user.LoginUserDto;
import com.airline.booking.dto.user.RegisterUserDto;
import com.airline.booking.dto.user.ResponseUserDto;
import com.airline.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseUserDto> registerUser(@RequestBody RegisterUserDto registerUserDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(registerUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginUserDto dto){
        return ResponseEntity.ok(userService.loginUser(dto));
    }

    @GetMapping("/test")
    public String hi(Authentication auth){
        return "Hi " + auth.getName() + " Role: " + auth.getAuthorities();
    }

}