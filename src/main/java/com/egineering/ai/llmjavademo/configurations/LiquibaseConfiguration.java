package com.egineering.ai.llmjavademo.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "liquibase.mongodb")
public record LiquibaseConfiguration(
        Boolean enabled,
        String changeLog,
        String url,
        String user,
        String password
) {

    @ConstructorBinding
    public LiquibaseConfiguration(Boolean enabled, String changeLog, String url, String user, String password) {
        this.enabled = enabled == null ? Boolean.TRUE : enabled;
        this.changeLog = changeLog;
        this.url = url;
        this.user = user;
        this.password = password;
    }
}

