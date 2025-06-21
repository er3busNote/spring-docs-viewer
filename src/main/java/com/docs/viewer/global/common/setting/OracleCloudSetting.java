package com.docs.viewer.global.common.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "oracle.cloud")
@Getter @Setter
public class OracleCloudSetting {

    @NotEmpty
    private String tenantId;

    @NotEmpty
    private String userId;

    @NotEmpty
    private String fingerprint;

    @NotEmpty
    private String namespace;

    @NotEmpty
    private String bucketName;

    @NotEmpty
    private String privateKey;
}
