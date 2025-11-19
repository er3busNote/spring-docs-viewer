package com.docs.viewer;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Exec;

import java.io.File;

public class LibreOfficeBuilder {

    private final Project project;

    public LibreOfficeBuilder(Project project) {
        this.project = project;
    }

    public void configure() {
        registerNativeBuildTask();
        registerCopyTask();
        project.getTasks().getByName("build").dependsOn("copyNativeToResources");
    }

    private void registerNativeBuildTask() {
        project.getTasks().register("nativeBuild", Exec.class, exec -> {

            String os = System.getProperty("os.name").toLowerCase();
            boolean isWin = os.contains("win");
            boolean isMac = os.contains("mac");
            boolean isLinux = os.contains("nux") || os.contains("linux");

            project.getLogger().lifecycle("🔧 LibreOffice JNI Build for: " + os);

            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome == null)
                throw new GradleException("JAVA_HOME not set");

            String includeBase = javaHome + "/include";
            String includePlatform = isMac ? "darwin" : (isWin ? "win32" : "linux");

            String loInclude, loLib, outputExt;

            if (isMac) {
                loInclude = "/Applications/LibreOffice.app/Contents/sdk/include";
                loLib = "/Applications/LibreOffice.app/Contents/Frameworks";
                outputExt = "dylib";
            } else if (isLinux) {
                loInclude = "/usr/lib/libreoffice/sdk/include";
                loLib = "/usr/lib/libreoffice/program";
                outputExt = "so";
            } else {
                loInclude = "C:/Program Files/LibreOffice/sdk/include";
                loLib = "C:/Program Files/LibreOffice/program";
                outputExt = "dll";
            }

            File nativeDir = new File(project.getProjectDir(), "native");
            File sourceFile = new File(nativeDir, "libreoffice_jni.c");
            File outputFile = new File(nativeDir, "libreoffice_jni." + outputExt);

            exec.setWorkingDir(nativeDir);
            exec.commandLine(
                    isWin ? "gcc.exe" : "gcc",
                    "-fPIC",
                    "-I" + includeBase,
                    "-I" + includeBase + "/" + includePlatform,
                    "-I" + loInclude,
                    "-shared",
                    sourceFile.getAbsolutePath(),
                    "-L" + loLib,
                    "-llibreofficekitgtk",
                    "-o", outputFile.getAbsolutePath()
            );
        });
    }

    private void registerCopyTask() {
        project.getTasks().register("copyNativeToResources", Copy.class, copy -> {
            copy.dependsOn("nativeBuild");

            String os = System.getProperty("os.name").toLowerCase();
            boolean isWin = os.contains("win");
            boolean isMac = os.contains("mac");
            boolean isLinux = os.contains("nux") || os.contains("linux");

            String targetDir = "src/main/resources/native/" +
                    (isLinux ? "linux" : isMac ? "macos" : "windows");

            copy.from("native");
            copy.include("*.so", "*.dll", "*.dylib");
            copy.into(targetDir);

            project.getLogger().lifecycle("📦 Copying native libs → " + targetDir);
        });
    }
}
