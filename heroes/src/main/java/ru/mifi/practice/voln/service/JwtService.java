package ru.mifi.practice.voln.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mifi.practice.voln.domain.entity.UserEntity;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String JWT_SECRET = "TiA+XVEuNl1vL0d9QFJ1RyZqUSEsIg==" +
        "Zk5LWEtJMklXInF2Y2ZHMnhAejA+JUorJA==" +
        "IHVrdVVyQ15fRXFwZjVnRV5BJn5XfDE4YQ==" +
        "VFx0UQ==" +
        "ey5yYWM=";

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Optional<UUID> extractUserId(String token) {
        return Optional.ofNullable(extractClaim(token, claims -> claims.get("user_id", String.class)))
            .map(UUID::fromString);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserEntity entity) {
            claims.put("user_id", entity.getId());
            claims.put("username", entity.getUsername());
            claims.put("email", entity.getEmail());
            claims.put("authorities", entity.getAuthorities());
        }
        return generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername()).issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
            .signWith(getSigningKey()).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
