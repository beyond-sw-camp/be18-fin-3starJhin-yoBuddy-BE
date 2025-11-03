package com.j3s.yobuddy.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@ConfigurationProperties(prefix= "app.jwt" )
public class JwtProperties {
    private String issuer;
    private String secret;
    private long accessExpirationMs;
    private long refreshExpirationMs;
}
