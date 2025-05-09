package com.docs.viewer.global.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "crypto-setting")
@Getter @Setter
public class CryptoProperties {

    @NotEmpty
    private String password;

    @NotEmpty
    private String salt;
}
