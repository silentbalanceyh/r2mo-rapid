package io.r2mo.spring.security;

import io.r2mo.SourceReflect;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lang : 2025-11-11
 */
public class PasswordEncode {

    public static void main(final String[] args) {
        final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final PasswordEncoder actual = SourceReflect.value(encoder, "passwordEncoderForEncode");
        System.out.println(actual.getClass().getName());
        final String pwd = "lang1017";
        System.out.println(encoder.encode(pwd));
    }

    public static String encode(final String pwd) {
        final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.encode(pwd);
    }
}
