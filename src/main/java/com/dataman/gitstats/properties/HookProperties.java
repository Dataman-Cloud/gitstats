package com.dataman.gitstats.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by biancl on 2017-09-24.
 */
@ConfigurationProperties(prefix="hook")
public class HookProperties {

    private String secretToken;

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }
}
