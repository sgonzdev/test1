package com.loginregister.test1.security.jwt;

import com.loginregister.test1.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private String secretKey = "z69UltLNPDeUtvwn+4/Pj7n+E66FEj4dNEGP0dnKKMEtmFnS1HF9JK64/B/zfm9W";
    private long timeExpiration = 86400000;
    private long timeRefreshExpiration = 604800000;


    public String generateToken(final UserEntity user) {
        return builToken(user, timeExpiration);
    }

    public String generateRefreshToken(final UserEntity user) {
        return builToken(user, timeRefreshExpiration);
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String builToken(final UserEntity user, long expiration) {
        return Jwts.builder()
                .claims(Map.of("name", user.getUsername()))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSingInKey())
                .compact();
    }

    public String extractUsername(final String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getSubject();

    }

    public boolean isTokenValid(String token, UserEntity user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
       return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getExpiration();
    }
}
