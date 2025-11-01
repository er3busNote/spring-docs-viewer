package com.docs.viewer.global.common.utils;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JasyptUtil {

    private final StringEncryptor jasyptEncryptor;
    private static StringEncryptor staticEncryptor;

    @PostConstruct
    public void init() {
        staticEncryptor = jasyptEncryptor;
    }

    private static String encrypt(String input) {
        return staticEncryptor.encrypt(input);
    }

    private static String decrypt(String encryptedText) {
        return staticEncryptor.decrypt(encryptedText);
    }

    private static String toEncrypt(Integer attachFile) {
        return encrypt(String.valueOf(attachFile));
    }

    private static String toDecrypt(String attachFile) {
        return decrypt(attachFile.replace(" ", "+"));
    }

    public static String getEncryptor(Integer reviewAttachImg) {
        return Optional.ofNullable(reviewAttachImg)
                .map(JasyptUtil::toEncrypt)
                .orElse("");
    }

    public static Integer getDecryptor(String attachFile) {
        return Optional.ofNullable(attachFile)
                .map(JasyptUtil::toDecrypt)
                .map(Integer::parseInt)
                .orElse(null);
    }
}
