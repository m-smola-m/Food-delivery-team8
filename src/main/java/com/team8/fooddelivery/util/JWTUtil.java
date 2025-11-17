package com.team8.fooddelivery.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtil {

    private static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 часа

    // Генерация токена для clientId
    public static String generateToken(Long clientId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(clientId))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_EXPIRATION_MS))
                .signWith(JWT_SECRET)
                .compact();
    }

    // Декодирование токена и получение clientId
    public static Long getClientIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | NumberFormatException e) {
            return null; // токен недействителен
        }
    }

    // Проверка валидности токена
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JWT_SECRET).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
