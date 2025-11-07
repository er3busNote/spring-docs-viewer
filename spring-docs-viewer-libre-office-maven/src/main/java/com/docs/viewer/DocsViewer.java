package com.docs.viewer;

public class DocsViewer {
    private long kitPtr;

    public DocsViewer() {
        String os = System.getProperty("os.name").toLowerCase();
        String libName;
        String resPath;

        if (os.contains("linux") || os.contains("nux")) {
            libName = "libreoffice_jni.so";
            resPath = "/native/linux/" + libName;
        } else if (os.contains("mac")) {
            libName = "libreoffice_jni.dylib";
            resPath = "/native/macos/" + libName;
        } else if (os.contains("win")) {
            libName = "libreoffice_jni.dll";
            resPath = "/native/windows/" + libName;
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }

        try {
            System.loadLibrary("libreoffice_jni");
        } catch (Throwable ignored) {
            NativeLibLoader.loadResourceLib(resPath, libName);
        }

        kitPtr = LibreOfficeKitNative.lokInit(null);
        if (kitPtr == 0) throw new IllegalStateException("Failed to init LibreOfficeKit");
    }

    public void convertToPdf(String inputPath, String outputPath) {
        long doc = LibreOfficeKitNative.lokDocumentLoad(kitPtr, inputPath);
        if (doc == 0) throw new RuntimeException("Failed to load document: " + inputPath);
        LibreOfficeKitNative.lokDocumentSaveAs(doc, outputPath, "pdf");
    }

    public void close() {
        if (kitPtr != 0) {
            LibreOfficeKitNative.lokDestroy(kitPtr);
            kitPtr = 0;
        }
    }
}
