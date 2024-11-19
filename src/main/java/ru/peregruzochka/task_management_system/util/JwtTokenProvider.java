package ru.peregruzochka.task_management_system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.property.JwtProperties;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        //TODO: поменять secret на env переменную;
        this.jwtProperties = jwtProperties;
        this.algorithm = Algorithm.HMAC256("secret");
        this.verifier = JWT.require(algorithm)
                .withIssuer(jwtProperties.getIssuer())
                .build();
    }

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
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

    private Date generateExpiresAt() {
        return new Date(System.currentTimeMillis() + (long) jwtProperties.getExpirationDays() * 24 * 60 * 60 * 1000);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getExpiresAt().after(new Date())
                && decodedJWT.getClaim("email").asString().equals(userDetails.getUsername());
    }
}
