package io.r2mo.spring.security;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lang : 2025-11-11
 */
public class SecurityPass {

    public static void main(final String[] args) {
        final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        final PasswordEncoder actual = SourceReflect.value(encoder, "passwordEncoderForEncode");
//        System.out.println(actual.getClass().getName());
//        final String pwd = "lang1017";
//        System.out.println(encoder.encode(pwd));
        final String secret = encoder.encode("r2mo-web-app-secret");
        System.out.println(secret);
        System.out.println(encoder.matches("r2mo-web-app-secret", secret));
    }

    public static String encode(final String pwd) {
        final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.encode(pwd);
    }
}
