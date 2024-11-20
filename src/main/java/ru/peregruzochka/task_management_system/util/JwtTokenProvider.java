package ru.peregruzochka.task_management_system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.entity.User;

import java.util.Date;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtTokenProvider {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    @Setter
    private String issuer;
    @Setter
    private long expirationDays;

    public JwtTokenProvider() {
        this.algorithm = Algorithm.HMAC256("secret");
        this.verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
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
