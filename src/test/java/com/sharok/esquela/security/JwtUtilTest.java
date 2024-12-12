package com.sharok.esquela.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.sharok.esquela.exception.InvalidTokenException;
import com.sharok.esquela.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    @InjectMocks private JwtUtil jwtUtil;

    @Mock private User user;

    private String base64EncodedSecretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        ReflectionTestUtils.setField(jwtUtil, "secretKey", base64EncodedSecretKey);
        ReflectionTestUtils.setField(jwtUtil, "jwtTokenValidity", 86400000);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        when(user.getId()).thenReturn("12345");
        when(user.getEmail()).thenReturn("user@test.com");
        when(user.getName()).thenReturn("Test User");
        when(user.getRole()).thenReturn(Set.of("Manager"));

        long startTime = System.currentTimeMillis();
        String token = jwtUtil.generateToken(user);
        long endTime = System.currentTimeMillis();

        assertNotNull(token);
        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(Base64.getDecoder().decode(base64EncodedSecretKey))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        assertEquals("12345", claims.getSubject());
        assertEquals("user@test.com", claims.get("email"));
        assertEquals("Test User", claims.get("name"));
        Object roles = claims.get("role");
        assertNotNull(roles, "Roles claim should not be null");
        assertEquals(Set.of("Manager"), new HashSet<>((List<String>) roles));

        Date expirationDate = claims.getExpiration();

        assertTrue(
                expirationDate.getTime() >= endTime + 599000,
                () ->
                        String.format(
                                "Token should not expire too early. Expiration: %d, Should be at least: %d",
                                expirationDate.getTime(), endTime + 599000));
    }

    @Test
    void validateToken_shouldReturnClaims_whenTokenIsValid() {
        String token =
                Jwts.builder()
                        .setSubject("12345")
                        .claim("email", "user@test.com")
                        .claim("name", "Test User")
                        .claim("roles", List.of("TdmsAdmin"))
                        .setIssuedAt(new Date())
                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + 1000 * 60 * 10)) // 10 minutes validity
                        .signWith(
                                SignatureAlgorithm.HS256,
                                Base64.getDecoder().decode(base64EncodedSecretKey))
                        .compact();

        Claims claims = jwtUtil.validateToken(token);

        assertNotNull(claims);
        assertEquals("12345", claims.getSubject());
        assertEquals("user@test.com", claims.get("email"));
        assertEquals("Test User", claims.get("name"));
        assertEquals(List.of("TdmsAdmin"), claims.get("roles"));
    }

    @Test
    void validateToken_shouldThrowInvalidTokenException_whenTokenIsExpired() {
        String expiredToken =
                Jwts.builder()
                        .setSubject("12345")
                        .claim("email", "user@test.com")
                        .claim("name", "Test User")
                        .claim("roles", List.of("TdmsAdmin"))
                        .setIssuedAt(
                                new Date(
                                        System.currentTimeMillis()
                                                - 1000 * 60 * 10)) // 10 minutes ago
                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                - 1000 * 60)) // Expired 1 minute ago
                        .signWith(
                                SignatureAlgorithm.HS256,
                                Base64.getDecoder().decode(base64EncodedSecretKey))
                        .compact();

        InvalidTokenException exception =
                assertThrows(
                        InvalidTokenException.class,
                        () -> {
                            jwtUtil.validateToken(expiredToken);
                        });

        assertEquals("Invalid Token", exception.getMessage());
    }
}
