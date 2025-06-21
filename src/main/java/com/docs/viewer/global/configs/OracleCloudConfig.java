package com.docs.viewer.global.configs;

import com.docs.viewer.global.common.setting.OracleCloudSetting;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.StringPrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class OracleCloudConfig {

    private final OracleCloudSetting oracleCloudSetting;

    @Bean
    public ObjectStorage objectStorageClient() {
        String privateKey = loadPrivateKey(oracleCloudSetting.getPrivateKey());
        Supplier<InputStream> privateKeySupplier = new StringPrivateKeySupplier(privateKey);
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                .tenantId(oracleCloudSetting.getTenantId())
                .userId(oracleCloudSetting.getUserId())
                .fingerprint(oracleCloudSetting.getFingerprint())
                .region(Region.AP_SEOUL_1)
                .privateKeySupplier(privateKeySupplier)
                .build();

        return ObjectStorageClient.builder()
                .region(Region.AP_SEOUL_1)
                .build(provider);
    }

    private static String loadPrivateKey(String filePath) {
        return Optional.of(Paths.get(filePath))
                .filter(Files::exists)
                .map(path -> {
                    try {
                        return new String(Files.readAllBytes(path));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).orElseThrow(() -> new RuntimeException("Oracle API Key 파일을 찾을 수 없습니다"));
    }
}
