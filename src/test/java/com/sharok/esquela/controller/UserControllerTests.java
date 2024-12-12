package com.sharok.esquela.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.sharok.esquela.BaseControllerTests;
import com.sharok.esquela.contract.response.UserResponse;
import com.sharok.esquela.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UserControllerTests extends BaseControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;

    @Test
    @SneakyThrows
    void testLogin_Success() {
        String idToken = "idToken456";
        String expectedAccessToken = "access_token_789";
        UserResponse mockUserResponse =
                UserResponse.builder().accessToken(expectedAccessToken).build();

        when(userService.getOrCreateUser(anyString())).thenReturn(mockUserResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/user/login")
                                .param("idToken", idToken)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.accessToken").value(expectedAccessToken));
    }
}
