#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h> // Linux 기준, Windows는 <windows.h>

typedef char* (*convert_func_t)(const char*, const char*);

typedef int (__stdcall *DOCX_FUNC)(const wchar_t*, const wchar_t*);
typedef int (__stdcall *PPTX_FUNC)(const wchar_t*, const wchar_t*);
typedef int (__stdcall *XLSX_FUNC)(const wchar_t*, const wchar_t*);

static HMODULE hModule = NULL;

JNIEXPORT jint JNICALL Java_ViewerLib_ConvertDocxToPng
  (JNIEnv* env, jobject obj, jstring inputPath, jstring outputDir)
{
    const jchar* in = (*env)->GetStringChars(env, inputPath, NULL);
    const jchar* out = (*env)->GetStringChars(env, outputDir, NULL);

    if (!hModule) hModule = LoadLibraryW(L"ViewerLib.dll");
    DOCX_FUNC f = (DOCX_FUNC) GetProcAddress(hModule, "ConvertDocxToPng");

    int result = f((const wchar_t*) in, (const wchar_t*) out);

    (*env)->ReleaseStringChars(env, inputPath, in);
    (*env)->ReleaseStringChars(env, outputDir, out);

    return result;
}

JNIEXPORT jint JNICALL Java_ViewerLib_ConvertPptxToPng
  (JNIEnv* env, jobject obj, jstring inputPath, jstring outputDir)
{
    const jchar* in = (*env)->GetStringChars(env, inputPath, NULL);
    const jchar* out = (*env)->GetStringChars(env, outputDir, NULL);

    if (!hModule) hModule = LoadLibraryW(L"ViewerLib.dll");
    PPTX_FUNC f = (PPTX_FUNC) GetProcAddress(hModule, "ConvertPptxToPng");

    int result = f((const wchar_t*) in, (const wchar_t*) out);

    (*env)->ReleaseStringChars(env, inputPath, in);
    (*env)->ReleaseStringChars(env, outputDir, out);

    return result;
}

JNIEXPORT jint JNICALL Java_ViewerLib_ConvertXlsxToPng
  (JNIEnv* env, jobject obj, jstring inputPath, jstring outputDir)
{
    const jchar* in = (*env)->GetStringChars(env, inputPath, NULL);
    const jchar* out = (*env)->GetStringChars(env, outputDir, NULL);

    if (!hModule) hModule = LoadLibraryW(L"ViewerLib.dll");
    XLSX_FUNC f = (XLSX_FUNC) GetProcAddress(hModule, "ConvertXlsxToPng");

    int result = f((const wchar_t*) in, (const wchar_t*) out);

    (*env)->ReleaseStringChars(env, inputPath, in);
    (*env)->ReleaseStringChars(env, outputDir, out);

    return result;
}