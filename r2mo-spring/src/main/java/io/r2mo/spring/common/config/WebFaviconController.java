package io.r2mo.spring.common.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lang : 2025-09-15
 */
@RestController
public class WebFaviconController {

    @GetMapping("favicon.ico")
    public void favicon() {
        /*
         * org.springframework.web.servlet.resource.NoResourceFoundException: No static resource favicon.ico
         */
    }
}
