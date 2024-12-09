package io.micronaut.session.http;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("micronaut.session.filter")
public class HttpSessionFilterConfiguration {
    private String excludePattern = "/**";

    public String getExcludePattern() {
        return excludePattern;
    }

    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }
}
