package com.sharok.esquela.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sharok.esquela.config.SecurityConfig;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@ExtendWith(MockitoExtension.class)
public class ExtractTokenTest {

    @Mock private SecurityConfig securityConfig;

    @Mock private JwtDecoder jwtDecoder;

    @InjectMocks private ExtractToken extractToken;

    private String testToken;

    @BeforeEach
    void setUp() {
        testToken =
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJvaWQiOiIxMjM0NTYiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QGV4YW1wbGUuY29tIiwibmFtZSI6IlRlc3QgVXNlciIsInJvbGVzIjpbIk1hbmFnZXIiLCJTdHVkZW50Il19.XanX5lIi6XHgHyMhAw5fTOfongta8J5AGW4vL6imxoY";
    }

    @Test
    void testExtractAzureAdId() {
        String azureAdId = extractToken.extractAzureAdId(testToken);
        assertEquals("123456", azureAdId);
    }

    @Test
    void testExtractEmail() {
        String email = extractToken.extractEmail(testToken);
        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractDisplayName() {
        String displayName = extractToken.extractName(testToken);
        assertEquals("Test User", displayName);
    }

    @Test
    void testExtractRoles() {
        Set<String> expectedRoles = new HashSet<>(Arrays.asList("Student", "Manager"));
        Set<String> roles = extractToken.extractRoles(testToken);
        assertEquals(expectedRoles, roles);
    }
}
