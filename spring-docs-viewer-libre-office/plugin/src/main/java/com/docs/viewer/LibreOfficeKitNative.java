package com.docs.viewer;

public class LibreOfficeKitNative {
    public static native long lokInit(String installPath);
    public static native long lokDocumentLoad(long kitPtr, String path);
    public static native void lokDocumentSaveAs(long docPtr, String outPath, String format);
    public static native void lokDestroy(long kitPtr);
}
