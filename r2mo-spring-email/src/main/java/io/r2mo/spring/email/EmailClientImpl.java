package io.r2mo.spring.email;

import io.r2mo.typed.json.JObject;
import org.springframework.stereotype.Service;

/**
 * @author lang : 2025-12-05
 */
@Service
public class EmailClientImpl implements EmailClient {

    @Override
    public JObject send(final String template, final JObject params) {
        return null;
    }
}
