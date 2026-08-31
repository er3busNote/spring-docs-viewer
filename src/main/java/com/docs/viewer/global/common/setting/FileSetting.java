package com.docs.viewer.global.common.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "file-setting")
@Getter @Setter
public class FileSetting {

    @NotEmpty
    private String filePath;

    @NotEmpty
    private String imagePath;
}
