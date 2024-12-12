package com.sharok.esquela.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import com.sharok.esquela.contract.response.UserResponse;
import com.sharok.esquela.model.User;
import com.sharok.esquela.repository.UserRepository;
import com.sharok.esquela.security.ExtractToken;
import com.sharok.esquela.security.JwtUtil;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock private ExtractToken extractToken;

    @Mock private UserRepository userRepository;

    @Mock private JwtUtil jwtUtil;

    @InjectMocks private UserService userService;

    private String authToken;
    private String idToken;
    private String azureAdId;
    private String email;
    private String displayName;
    private Set<String> roles;
    private String customToken;

    @BeforeEach
    void setUp() {
        authToken = "auth_token";
        idToken = "id_token";
        azureAdId = "azure_ad_id";
        email = "user@example.com";
        displayName = "Test User";
        roles = Set.of("Student", "Manager");
        customToken = "custom_jwt_token";

        when(extractToken.extractAzureAdId(idToken)).thenReturn(azureAdId);
        when(extractToken.extractEmail(idToken)).thenReturn(email);
        when(extractToken.extractName(idToken)).thenReturn(displayName);
        when(extractToken.extractRoles(idToken)).thenReturn(roles);
        when(jwtUtil.generateToken(any(User.class))).thenReturn(customToken);
    }

    @Test
    void getOrCreateUser_ExistingUser_ReturnsUserResponse() {
        User existingUser =
                User.builder().id(azureAdId).email(email).name(displayName).role(roles).build();

        when(userRepository.findById(azureAdId)).thenReturn(Optional.of(existingUser));

        UserResponse response = userService.getOrCreateUser(idToken);

        assertEquals(customToken, response.getAccessToken());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil).generateToken(existingUser);
    }

    @Test
    void getOrCreateUser_NewUser_CreatesAndReturnsUserResponse() {
        when(userRepository.findById(azureAdId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.getOrCreateUser(idToken);

        assertEquals(customToken, response.getAccessToken());
        verify(userRepository)
                .save(
                        argThat(
                                user ->
                                        user.getId().equals(azureAdId)
                                                && user.getEmail().equals(email)
                                                && user.getName().equals(displayName)
                                                && user.getRole().equals(roles)));
        verify(jwtUtil).generateToken(any(User.class));
    }
}
