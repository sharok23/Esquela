package com.sharok.esquela.contract.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserResponse {
    private String accessToken;
}
