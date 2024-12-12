package com.sharok.esquela.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtractToken {

    public String extractAzureAdId(String idToken) {
        return decodeJwtToken(idToken).getClaim("oid").asString();
    }

    public String extractEmail(String idToken) {
        return decodeJwtToken(idToken).getClaim("preferred_username").asString();
    }

    public String extractName(String idToken) {
        return decodeJwtToken(idToken).getClaim("name").asString();
    }

    public Set<String> extractRoles(String idToken) {
        Claim rolesClaim = decodeJwtToken(idToken).getClaim("roles");
        if (rolesClaim.isNull()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(rolesClaim.asArray(String.class)));
    }

    public DecodedJWT decodeJwtToken(String idToken) {
        String cleanToken = idToken.trim().replace("Bearer ", "");
        return JWT.decode(cleanToken);
    }
}
