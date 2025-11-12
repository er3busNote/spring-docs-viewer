package com.docs.viewer;

public class ViewerApplication {
    static {
        System.loadLibrary("libreoffice_jni"); // libreoffice_jni.so 또는 .dll
    }

    // JNI 함수 선언
    private native long initLibreOffice(String installPath);
    private native long loadDocument(long officeHandle, String filePath);
    private native void exportToPdf(long documentHandle, String outputPath);
    private native void destroy(long officeHandle);

    // Wrapper 메서드
    public void convertToPdf(String inputPath, String outputPath) {
        long office = initLibreOffice(null);
        long doc = loadDocument(office, inputPath);
        exportToPdf(doc, outputPath);
        destroy(office);
    }

    public static void main(String[] args) {
        ViewerApplication viewer = new ViewerApplication();
        viewer.convertToPdf("/path/to/input.pptx", "/path/to/output.pdf");
        System.out.println("✅ Conversion complete!");
    }
}
