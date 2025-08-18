package com.haupsystem.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.haupsystem.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;

@Component
public class JWTUtil {

    /*@Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(UserSpringSecurity user) {
        SecretKey key = getKeyBySecret();
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("nome", user.getNome())
                .claim("tipo", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
                .setExpiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(key)
                .compact();
    }

    private SecretKey getKeyBySecret() {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());
        return key;
    }

    public boolean isValidToken(String token) {
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)) {
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if (Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate))
                return true;
        }
        return false;
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims))
            return claims.getSubject();
        return null;
    }

    private Claims getClaims(String token) {
        SecretKey key = getKeyBySecret();
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }*/
	
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration-ms}") // ex.: 15 * 60 * 1000
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration-ms}") // ex.: 7 * 24 * 60 * 60 * 1000
    private long refreshExpirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarAccessToken(UserSpringSecurity user) {
        Instant agora = Instant.now();
        Instant exp = agora.plusMillis(accessExpirationMs);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti) // jti
                .setSubject(user.getUsername())
                .claim("uid", user.getId())
                .claim("name", user.getNome())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(Date.from(agora))
                .setExpiration(Date.from(exp))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String gerarRefreshToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant exp = agora.plusMillis(refreshExpirationMs);
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .claim("uid", usuario.getId())
                .claim("name", usuario.getNome())
                .setIssuedAt(Date.from(agora))
                .setExpiration(Date.from(exp))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean expirado(String token) {
        try {
            return parse(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public String getUsername(String token) { return parse(token).getSubject(); }
    public String getJti(String token) { return parse(token).getId(); }
	
}
