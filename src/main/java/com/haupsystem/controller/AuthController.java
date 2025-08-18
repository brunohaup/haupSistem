package com.haupsystem.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haupsystem.security.JWTUtil;
import com.haupsystem.service.AutenticacaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticacaoService authService;
    private final JWTUtil jwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Map<String, Object> tokens = authService.login(body.get("username"), body.get("password"));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        Map<String, Object> tokens = authService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body,
                                    @RequestHeader(value="Authorization", required=false) String authHeader) {
        String jti = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String access = authHeader.substring(7);
            try { jti = jwt.getJti(access); } catch (Exception ignored) {}
        }
        authService.logout(body.get("refreshToken"), jti);
        return ResponseEntity.noContent().build();
    }
}

