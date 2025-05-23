package com.audition.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("api-config")
@Component
@Getter
@Setter
public class ApiConfiguration {
    private ApiClientConfiguration auditionApi;
}
