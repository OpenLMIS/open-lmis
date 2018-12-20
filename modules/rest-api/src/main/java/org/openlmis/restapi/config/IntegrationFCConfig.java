package org.openlmis.restapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationFCConfig {

    @Value("${fc.integration.url}")
    private String url;

    @Value("${fc.integration.key}")
    private String key;

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }
}
