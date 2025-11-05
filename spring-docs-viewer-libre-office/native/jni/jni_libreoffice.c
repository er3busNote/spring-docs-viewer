#include <jni.h>
#include <LibreOfficeKit/LibreOfficeKit.h>
#include <stdio.h>

JNIEXPORT jlong JNICALL Java_DocsViewer_initLibreOffice
  (JNIEnv *env, jobject obj, jstring installPath) {
    const char *path = (*env)->GetStringUTFChars(env, installPath, 0);
    LibreOfficeKit *kit = lok_init(path);
    (*env)->ReleaseStringUTFChars(env, installPath, path);
    return (jlong)kit;
}

JNIEXPORT jlong JNICALL Java_DocsViewer_loadDocument
  (JNIEnv *env, jobject obj, jlong officeHandle, jstring filePath) {
    const char *path = (*env)->GetStringUTFChars(env, filePath, 0);
    LibreOfficeKit *kit = (LibreOfficeKit *)officeHandle;
    LibreOfficeKitDocument *doc = kit->pClass->documentLoad(kit, path);
    (*env)->ReleaseStringUTFChars(env, filePath, path);
    return (jlong)doc;
}

JNIEXPORT void JNICALL Java_DocsViewer_exportToPdf
  (JNIEnv *env, jobject obj, jlong docHandle, jstring outputPath) {
    const char *path = (*env)->GetStringUTFChars(env, outputPath, 0);
    LibreOfficeKitDocument *doc = (LibreOfficeKitDocument *)docHandle;
    doc->pClass->saveAs(doc, path, "pdf", "{}");
    (*env)->ReleaseStringUTFChars(env, outputPath, path);
}

JNIEXPORT void JNICALL Java_DocsViewer_destroy
  (JNIEnv *env, jobject obj, jlong officeHandle) {
    LibreOfficeKit *kit = (LibreOfficeKit *)officeHandle;
    kit->pClass->destroy(kit);
}