package com.vinay.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinay.dto.LogInDTO;
import com.vinay.dto.UserDTO;
import com.vinay.entity.User;
import com.vinay.payload.ApiResponse;
import com.vinay.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody UserDTO usersDto) {

        UserDTO dto = userService.createUser(usersDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        201,
                        "Registration successful. Check email for verification.",
                        dto
                ));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,Object>>> loginUser(
            @RequestBody LogInDTO logInDTO) {

        User user = userService.getUserByEmail(logInDTO.getEmail());

        String token = userService.verify(logInDTO);

        Map<String,Object> data = new HashMap<>();
        data.put("token", token);
        data.put("email", user.getEmail());
        data.put("role", user.getRole());

        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                200,
                "Login successful",
                data
            )
        );
    }

    // Verify Account
    @GetMapping("/verify-account")
    public void verifyAccount(
            @RequestParam("email") String email,
            HttpServletResponse response) throws IOException {

        userService.verifyAccount(email);

        response.sendRedirect("http://43.204.68.152:3000/login");
    }
}