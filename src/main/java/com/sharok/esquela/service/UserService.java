package com.sharok.esquela.service;

import com.sharok.esquela.contract.response.UserResponse;
import com.sharok.esquela.model.User;
import com.sharok.esquela.repository.UserRepository;
import com.sharok.esquela.security.ExtractToken;
import com.sharok.esquela.security.JwtUtil;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExtractToken extractToken;
    private final JwtUtil jwtUtil;

    public UserResponse getOrCreateUser(String idToken) {
        String azureAdId = extractToken.extractAzureAdId(idToken);
        String email = extractToken.extractEmail(idToken);
        String name = extractToken.extractName(idToken);
        Set<String> role = extractToken.extractRoles(idToken);
        User user =
                userRepository
                        .findById(azureAdId)
                        .orElseGet(
                                () -> {
                                    User newUser =
                                            User.builder()
                                                    .id(azureAdId)
                                                    .email(email)
                                                    .name(name)
                                                    .role(role)
                                                    .build();
                                    return userRepository.save(newUser);
                                });
        String customToken = jwtUtil.generateToken(user);
        return UserResponse.builder().accessToken(customToken).build();
    }
}
