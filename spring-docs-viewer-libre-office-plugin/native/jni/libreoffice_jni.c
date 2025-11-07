#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <LibreOfficeKit/LibreOfficeKit.h>

#define TO_JLONG(p) ((jlong)(intptr_t)(p))
#define FROM_JLONG(type, v) ((type*)(intptr_t)(v))

JNIEXPORT jlong JNICALL Java_com_docs_viewer_LibreOfficeKitNative_lokInit
  (JNIEnv *env, jclass cls, jstring jpath) {
    const char *path = NULL;
    if (jpath != NULL) {
        path = (*env)->GetStringUTFChars(env, jpath, NULL);
    }
    LibreOfficeKit *kit = lok_init(path);
    if (jpath != NULL) (*env)->ReleaseStringUTFChars(env, jpath, path);
    return TO_JLONG(kit);
}

JNIEXPORT jlong JNICALL Java_com_docs_viewer_LibreOfficeKitNative_lokDocumentLoad
  (JNIEnv *env, jclass cls, jlong kitPtr, jstring jfilepath) {
    LibreOfficeKit *kit = FROM_JLONG(LibreOfficeKit, kitPtr);
    const char *path = (*env)->GetStringUTFChars(env, jfilepath, NULL);
    LibreOfficeKitDocument *doc = kit->pClass->documentLoad(kit, path);
    (*env)->ReleaseStringUTFChars(env, jfilepath, path);
    return TO_JLONG(doc);
}

JNIEXPORT void JNICALL Java_com_docs_viewer_LibreOfficeKitNative_lokDocumentSaveAs
  (JNIEnv *env, jclass cls, jlong docPtr, jstring joutPath, jstring jformat) {
    LibreOfficeKitDocument *doc = FROM_JLONG(LibreOfficeKitDocument, docPtr);
    const char *out = (*env)->GetStringUTFChars(env, joutPath, NULL);
    const char *fmt = (*env)->GetStringUTFChars(env, jformat, NULL);
    doc->pClass->saveAs(doc, out, fmt, "{}");
    (*env)->ReleaseStringUTFChars(env, joutPath, out);
    (*env)->ReleaseStringUTFChars(env, jformat, fmt);
}

JNIEXPORT void JNICALL Java_com_docs_viewer_LibreOfficeKitNative_lokDestroy
  (JNIEnv *env, jclass cls, jlong kitPtr) {
    LibreOfficeKit *kit = FROM_JLONG(LibreOfficeKit, kitPtr);
    kit->pClass->destroy(kit);
}