package com.docs.viewer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;

public class ViewerPlugin_bak implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // DSL 등록
        ViewerExtension ext = project.getExtensions()
                .create("docsViewer", ViewerExtension.class);

        // 네이티브 빌드 태스크 생성
        Task docsViewerBuild = project.task("docsViewerBuild", task -> {
            task.setGroup("docs-viewer");
            task.setDescription("Builds C#, Go, and JNI libraries for Docs Viewer");

            task.doLast(t -> {
                String os = System.getProperty("os.name").toLowerCase();
                boolean isWin = os.contains("win");
                boolean isMac = os.contains("mac");
                boolean isLinux = os.contains("nux") || os.contains("linux");

                project.getLogger().lifecycle("📦 DocsViewer Native Build - Detected OS: " + os);

                File outputDir = ext.getOutputDir() != null ?
                        ext.getOutputDir() :
                        new File(project.getBuildDir(), "native");

                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }

                // C# 빌드
                if (ext.isEnableCs()) {
                    String runtime = isWin ? "win-x64" : isMac ? "osx-x64" : "linux-x64";
                    project.exec(execSpec -> {
                        execSpec.workingDir("cs");
                        execSpec.commandLine("dotnet", "publish", "-c", "Release", "-r", runtime,
                                "/p:PublishAot=true", "/p:SelfContained=true");
                    });
                    project.getLogger().lifecycle("✅ C# build completed");
                }

                // Go 빌드
                if (ext.isEnableGo()) {
                    String goLib = isWin ? "libdocsviewer.dll" : isMac ? "libdocsviewer.dylib" : "libdocsviewer.so";
                    project.exec(execSpec -> {
                        execSpec.workingDir("go");
                        execSpec.commandLine("go", "build", "-buildmode=c-shared",
                                "-o", new File(outputDir, goLib).getAbsolutePath(),
                                "bridge.go");
                    });
                    project.getLogger().lifecycle("✅ Go build completed");
                }

                // JNI 빌드
                if (ext.isEnableJni()) {
                    String jniLib = isWin ? "jni_docsviewer.dll"
                            : isMac ? "libjni_docsviewer.dylib"
                            : "libjni_docsviewer.so";
                    project.exec(execSpec -> {
                        execSpec.workingDir("jni");
                        execSpec.commandLine("gcc", "-fPIC", "-shared",
                                "-o", new File(outputDir, jniLib).getAbsolutePath(),
                                "native/jni_shim.c", "-ldl");
                    });
                    project.getLogger().lifecycle("✅ JNI build completed");
                }

                project.getLogger().lifecycle("🎉 DocsViewer native build finished successfully!");
            });
        });

        // 기본 빌드와 연결
        project.getTasks().getByName("build").dependsOn(docsViewerBuild);
    }
}
