package com.docs.viewer.global.common.setting;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "crypto-setting")
@Getter @Setter
public class CryptoSetting {

    @NotEmpty
    private String password;

    @NotEmpty
    private String salt;
}
