package com.docs.viewer.global.common.utils;

import com.docs.viewer.global.common.setting.CryptoSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

@Component
@RequiredArgsConstructor
public class CryptoUtil {

    private static final int ITERATIONS = 10000;
    private static final int KEY_SIZE = 256;
    private static final int IV_LENGTH = 16;

    private final CryptoSetting cryptoSetting;

    private static String password;
    private static String salt;

    @PostConstruct
    public void init() {
        password = cryptoSetting.getPassword();
        salt = cryptoSetting.getSalt();
    }

    // 참고 (Java AES Encryption and Decryption) : https://howtodoinjava.com/java/java-security/aes-256-encryption-decryption/
    public static void encryptFile(MultipartFile file, File targetFile) throws Exception {
        // 파일 읽기
        byte[] fileBytes = file.getBytes();

        // 키 생성
        SecretKey key = generateKey(password, salt);

        // 초기화 벡터 생성
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // AES 암호화
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encryptedFileBytes = cipher.doFinal(fileBytes);

        // 암호화된 파일 저장
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(iv);
            outputStream.write(encryptedFileBytes);
        }
    }

    public static byte[] decryptFile(String inputFilePath) throws Exception {
        // 암호화된 파일 읽기
        byte[] encryptedFileBytesWithIV = Files.readAllBytes(Paths.get(inputFilePath));

        // 키 생성
        SecretKey key = generateKey(password, salt);

        // 초기화 벡터 추출
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(encryptedFileBytesWithIV, 0, iv, 0, IV_LENGTH);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // AES 복호화
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        // 복호화된 파일 리턴
        return cipher.doFinal(encryptedFileBytesWithIV, IV_LENGTH, encryptedFileBytesWithIV.length - IV_LENGTH);
    }

    private static SecretKey generateKey(String password, String salt) throws Exception {
        // PBKDF2 키 생성
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}
