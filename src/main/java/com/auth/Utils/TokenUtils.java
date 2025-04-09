package com.auth.Utils;

import java.security.SecureRandom;
import java.util.Base64;

//unused may be
public class TokenUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken() {
        byte[] randomBytes = new byte[24];  // 24-byte length (for a 192-bit token)
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);  // Make it URL safe
    }
}