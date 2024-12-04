package com.loginregister.test1.Controllers;

import com.loginregister.test1.Controllers.dto.LoginDTO;
import com.loginregister.test1.Controllers.dto.RegisterDTO;
import com.loginregister.test1.Controllers.dto.TokenDTO;
import com.loginregister.test1.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody final RegisterDTO registerDTO){
        final TokenDTO token = service.register(registerDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody final LoginDTO loginDTO){
        final TokenDTO token = service.login(loginDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public TokenDTO refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return service.refreshToken(authHeader);
    }
}
