#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h> // Linux 기준, Windows는 <windows.h>

typedef char* (*convert_func_t)(const char*, const char*);

JNIEXPORT jstring JNICALL Java_ViewerLibJNI_convertDocxToPng(JNIEnv *env, jobject obj, jstring jinput, jstring joutput)
{
    const char *input = (*env)->GetStringUTFChars(env, jinput, NULL);
    const char *output = (*env)->GetStringUTFChars(env, joutput, NULL);

    void *lib = dlopen("./ViewerLib.so", RTLD_LAZY);
    if (!lib) {
        return (*env)->NewStringUTF(env, "ERR: failed to load ViewerLib.so");
    }

    convert_func_t func = (convert_func_t)dlsym(lib, "ConvertDocxToPng");
    if (!func) {
        dlclose(lib);
        return (*env)->NewStringUTF(env, "ERR: symbol not found");
    }

    char *result = func(input, output);
    jstring jres = (*env)->NewStringUTF(env, result);

    free(result); // ConvertDocxToPng 내부에서 AllocCoTaskMem → 필요 시 CoTaskMemFree로 변경
    dlclose(lib);

    (*env)->ReleaseStringUTFChars(env, jinput, input);
    (*env)->ReleaseStringUTFChars(env, joutput, output);
    return jres;
}