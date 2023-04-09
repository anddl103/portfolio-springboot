package com.hybe.larva.config.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.UserDetail;
import com.hybe.larva.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class FirebaseTokenProvider {


    /**
     *
     * @param authToken Firebase access token string
     * @return the computed result
     * @throws Exception
     */
    public Authentication getAndValidateAuthentication(String authToken) throws Exception {

        UsernamePasswordAuthenticationToken authentication;

        FirebaseToken firebaseToken = authenticateFirebaseToken(authToken);

        UserDetail userDetail = getUserDetails(firebaseToken);

        authentication = new UsernamePasswordAuthenticationToken(firebaseToken, authToken, userDetail.getAuthorities());

        authentication.setDetails(userDetail);

        return authentication;
    }

    /**
     * @param authToken Firebase access token string
     * @return the computed result
     * @throws Exception
     */
    public FirebaseToken authenticateFirebaseToken(String authToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(authToken);
    }

    private UserDetail getUserDetails(FirebaseToken firebaseToken) {
        String role = UserRole.ROLE_USER.getCode();

        Map<String, Object> claims = firebaseToken.getClaims();
        for (String mapkey : claims.keySet()){
            if (LarvaConst.ADMIN_ROLE.equals(mapkey)) {
                role = claims.get(mapkey).toString();
            }
        }

        return UserDetail.CreateUserDetail(firebaseToken.getUid(), role);
    }
}
