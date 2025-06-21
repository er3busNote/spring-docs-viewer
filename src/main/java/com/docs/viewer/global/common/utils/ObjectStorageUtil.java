package com.docs.viewer.global.common.utils;

import com.docs.viewer.global.common.setting.OracleCloudSetting;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ObjectStorageUtil {

    private final ObjectStorage objectStorage;
    private final OracleCloudSetting oracleCloudSetting;

    public void uploadFile(MultipartFile file, String objectName) throws IOException {

        String contentType = file.getContentType();
        byte[] bytes = file.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(oracleCloudSetting.getNamespace())
                .bucketName(oracleCloudSetting.getBucketName())
                .objectName(objectName)
                .putObjectBody(inputStream)
                .contentLength((long) bytes.length)
                .contentType(contentType)
                .build();

        objectStorage.putObject(request);
    }

    public byte[] downloadFile(String objectName) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .namespaceName(oracleCloudSetting.getNamespace())
                .bucketName(oracleCloudSetting.getBucketName())
                .objectName(objectName)
                .build();

        GetObjectResponse response = objectStorage.getObject(request);
        InputStream inputStream = response.getInputStream();

        return convertToByteArray(inputStream);
    }

    private static byte[] convertToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
