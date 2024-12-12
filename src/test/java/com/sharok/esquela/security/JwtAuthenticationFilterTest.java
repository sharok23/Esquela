package com.sharok.esquela.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock private JwtUtil jwtUtil;

    @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @SneakyThrows
    void testValidToken() {
        String token = "validToken";
        String userId = "testUser";
        Claims claims = mock(Claims.class);

        when(claims.getSubject()).thenReturn(userId);
        when(claims.get("role", List.class)).thenReturn(Arrays.asList("Teacher", "Student"));
        when(jwtUtil.validateToken(token)).thenReturn(claims);

        request.addHeader("Authorization", "Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("Teacher")));
        assertTrue(
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("Student")));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalidToken";

        when(jwtUtil.validateToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid JWT token"));

        request.addHeader("Authorization", "Bearer " + invalidToken);

        assertThrows(
                RuntimeException.class,
                () -> {
                    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
                });
    }

    @Test
    @SneakyThrows
    void testNoAuthorizationHeader() {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    void testNonBearerToken() {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
