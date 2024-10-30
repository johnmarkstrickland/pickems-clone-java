package com.pickems.backend.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTestUtils {

    @Value("${token.signing.key}")
    private String jwtSecret;

    public String generateToken(String subject, Map<String, Object> claims) {
        try {
            // Create HMAC signer
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());

            // Prepare JWT with claims
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                                                 .subject(subject)
                                                 .issueTime(new Date())
                                                 .expirationTime(new Date(System.currentTimeMillis() + 3600000));

            // Add custom claims
            claims.forEach(claimsBuilder::claim);

            // Create signed JWT
            SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claimsBuilder.build()
            );

            // Sign the JWT
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error generating JWT token", e);
        }
    }
}
