package com.qmasters.fila_flex.service;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.qmasters.fila_flex.model.RevokedToken;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.RevokedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Value("${api.security.token.secret}")
    private String secret;

    private long expiration = (48 * 60 * 60 * 1000);//tempo de expiração de 48 horas

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            String token = JWT.create()
                .withIssuer("FilaFlex")
                .withSubject(user.getUsername())
                .withExpiresAt(this.getExpirationAt())
                .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao criar o token JWT em TokenService: " + e.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer("FilaFlex")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "Erro no validateToken, em TokenService";
        }
    }

    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token)) {
            revokedTokenRepository.save(new RevokedToken(token));
        }
    }

    private Claims extractAllClaims(String token) {//sem uso no momento
        return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
    
    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    private Date getExpirationAt() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}
