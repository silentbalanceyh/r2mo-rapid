package io.r2mo.spring.security.ldap;

/**
 * @author lang : 2025-12-09
 */
public interface LdapService {

    LdapLoginRequest validate(LdapLoginRequest request);
}
