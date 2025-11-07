package com.docs.viewer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLibLoader {
    public static void loadLibraryFromPath(Path libPath) {
        System.load(libPath.toAbsolutePath().toString());
    }

    public static void loadResourceLib(String resourcePath, String libName) {
        try (InputStream in = NativeLibLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new UnsatisfiedLinkError("Resource not found: " + resourcePath);
            Path temp = Files.createTempDirectory("lojni");
            temp.toFile().deleteOnExit();
            Path libFile = temp.resolve(libName);
            Files.copy(in, libFile, StandardCopyOption.REPLACE_EXISTING);
            libFile.toFile().deleteOnExit();
            System.load(libFile.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new UnsatisfiedLinkError("Failed to extract native lib: " + e.getMessage());
        }
    }
}
