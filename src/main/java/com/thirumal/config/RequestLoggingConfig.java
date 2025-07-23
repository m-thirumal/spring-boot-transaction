package com.thirumal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author Thirumal M
 */
@Configuration
public class RequestLoggingConfig {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Bean
    CommonsRequestLoggingFilter requestLoggingFilter() {
        logger.info("Initializing request logging filter");
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setBeforeMessagePrefix("Request: ");
        filter.setAfterMessagePrefix("Response: ");
        return filter;
    }
}
