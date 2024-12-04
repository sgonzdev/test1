package com.loginregister.test1.services;


import com.loginregister.test1.Controllers.dto.LoginDTO;
import com.loginregister.test1.Controllers.dto.RegisterDTO;
import com.loginregister.test1.Controllers.dto.TokenDTO;
import com.loginregister.test1.entities.TokenEntity;
import com.loginregister.test1.entities.UserEntity;
import com.loginregister.test1.repository.TokenRepository;
import com.loginregister.test1.repository.UserRepository;
import com.loginregister.test1.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final UserRepository UserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public  TokenDTO register(RegisterDTO registerDTO) {
        var user = UserEntity.builder()
                .username(registerDTO.username())
                .password(passwordEncoder.encode(registerDTO.password()))
                .email(registerDTO.email())
                .build();
        var savedUser = UserRepository.save(user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        savedUserToken(savedUser, refreshToken);
        return new TokenDTO(jwt, refreshToken);
    }


    public TokenDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.email(),
                        loginDTO.password()
                )
        );

        var user = UserRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, jwt);
        return new TokenDTO(jwt, refreshToken);
    }

    private void savedUserToken(UserEntity user, String jwtToken) {
        var token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenEntity.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

    }

    private void revokeAllUserTokens(final UserEntity user) {
        final List<TokenEntity> validUserTokens = tokenRepository
                .findByUserIdAndExpiredFalseAndRevokedFalse(user.getId());
        if (!validUserTokens.isEmpty()) {
            for (final TokenEntity token : validUserTokens) {
                token.setRevoked(true);
                token.setExpired(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }


    public TokenDTO refreshToken(final String authHeader) {
        if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        final UserEntity user = UserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid token");
        }
        final String accessToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, refreshToken);
        return new TokenDTO(accessToken, refreshToken);
    }


}
