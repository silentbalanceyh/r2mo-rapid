package io.r2mo.spring.security.ldap;

import io.r2mo.function.Fn;
import io.r2mo.spring.security.ldap.exception._80402Exception401AuthFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * 负责更改 ID 相关信息
 *
 * @author lang : 2025-12-09
 */
@Service
@Slf4j
public class LdapServiceImpl implements LdapService {
    @Autowired
    private LdapConfig config;
    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public LdapLoginRequest validate(final LdapLoginRequest request) {
        // 1. 获取用户输入的账号和密码
        final String account = request.getUsername();
        final String password = request.getPassword();


        // 2. 构造“万能”查询条件
        final LdapQuery query = LdapQueryBuilder.query()
            .filter(this.config.getUserFilter(), account);


        // 3. 执行认证
        final boolean authenticated;
        try {
            authenticated = this.ldapTemplate.authenticate(query.base(), query.filter().toString(), password);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new _80402Exception401AuthFailure(account);
        }

        Fn.jvmKo(!authenticated, _80402Exception401AuthFailure.class, account);


        // 4. 提取真实的 email
        final String email = this.ldapTemplate.searchForObject(query,
            ctx -> ((DirContextAdapter) ctx).getStringAttribute(this.config.getUserEmail()));
        final String uid = this.ldapTemplate.searchForObject(query,
            ctx -> ((DirContextAdapter) ctx).getStringAttribute(this.config.getUserId()));
        /*
         * 由于 email 具有更强制的唯一性，所以此处的
         * 属性映射：
         * - id = email
         * - uid = LDAP 中的标识信息
         */
        request.setUid(uid);
        request.setId(email);
        return request;
    }
}
