package com.docs.viewer;

public class ViewerLib {

    static {
        System.loadLibrary("ViewerLib");
    }

    public native int ConvertDocxToPng(String inputPath, String outputDir);
    public native int ConvertPptxToPng(String inputPath, String outputDir);
    public native int ConvertXlsxToPng(String inputPath, String outputDir);
}
