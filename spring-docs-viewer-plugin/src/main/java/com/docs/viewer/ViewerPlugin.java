package com.docs.viewer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.process.ExecSpec;

import java.nio.file.Files;
import java.nio.file.Path;

public class ViewerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("docsViewer", ViewerExtension.class);

        Task docsViewerBuild = project.task("docsViewerBuild", task -> {
            task.setGroup("docs-viewer");
            task.setDescription("Builds JNI native library for Docs Viewer");

            task.doLast(t -> {
                try {
                    // OS 감지
                    String os = System.getProperty("os.name").toLowerCase();
                    boolean isWin = os.contains("win");
                    boolean isMac = os.contains("mac");
                    boolean isLinux = os.contains("nux") || os.contains("linux");

                    String javaHome = System.getenv("JAVA_HOME");
                    if (javaHome == null) throw new RuntimeException("JAVA_HOME not set!");

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
                    } else if (isWin) {
                        loInclude = "C:/Program Files/LibreOffice/sdk/include";
                        loLib = "C:/Program Files/LibreOffice/program";
                        outputExt = "dll";
                    } else {
                        throw new RuntimeException("Unsupported OS: " + os);
                    }

                    Path nativeDir = project.getProjectDir().toPath().resolve("native");
                    if (!Files.exists(nativeDir)) Files.createDirectories(nativeDir);

                    Path sourceFile = nativeDir.resolve("libreoffice_jni.c");
                    if (!Files.exists(sourceFile))
                        throw new RuntimeException("Missing JNI source file: " + sourceFile);

                    String outputName = "libreoffice_jni." + outputExt;
                    Path outputPath = nativeDir.resolve(outputName);

                    // 이미 빌드되어 있으면 스킵
                    if (Files.exists(outputPath)) {
                        project.getLogger().lifecycle("✅ Native library already built: " + outputPath);
                        return;
                    }

                    project.getLogger().lifecycle("🔧 Building JNI library using Gradle exec()...");

                    // Gradle의 exec() 사용
                    project.exec((ExecSpec execSpec) -> {
                        execSpec.setWorkingDir(nativeDir.toFile());

                        execSpec.commandLine(
                                isWin ? "gcc.exe" : "gcc",
                                "-fPIC",
                                "-I" + includeBase,
                                "-I" + includeBase + "/" + includePlatform,
                                "-I" + loInclude,
                                "-shared",
                                sourceFile.toString(),
                                "-L" + loLib,
                                "-llibreofficekitgtk",
                                "-o", outputPath.toString()
                        );
                    });

                    project.getLogger().lifecycle("✅ Native build complete: " + outputPath);

                } catch (Exception e) {
                    throw new RuntimeException("JNI build failed: " + e.getMessage(), e);
                }
            });
        });

        project.getTasks().getByName("build").dependsOn(docsViewerBuild);
    }
}