package ru.peregruzochka.task_management_system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.entity.User;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration-days}")
    private long expirationDays;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secretKey);
        verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("id", user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .withExpiresAt(generateExpiresAt())
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        return verifier.verify(token).getClaim("email").asString();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(verifier.verify(token).getClaim("id").asString());
    }

    private Date generateExpiresAt() {
        return new Date(System.currentTimeMillis() + expirationDays * 24 * 60 * 60 * 1000);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getExpiresAt().after(new Date())
                && decodedJWT.getClaim("email").asString().equals(userDetails.getUsername());
    }
}
