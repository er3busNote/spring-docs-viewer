package com.docs.viewer.global.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "file-setting")
@Getter @Setter
public class FileProperties {

    @NotEmpty
    private String filepath;
}
