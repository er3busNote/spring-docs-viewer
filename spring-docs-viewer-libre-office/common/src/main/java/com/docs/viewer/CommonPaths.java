package com.docs.viewer;

import java.io.File;
import java.nio.file.Path;
import org.gradle.api.Project;

public final class CommonPaths {

    private CommonPaths() {}

    public static Path resolveNativePath(Project project) {
        return project.getRootProject()
                .file("spring-docs-viewer-libre-office/common")
                .toPath()
                .resolve("native");
    }

    public static File resolveNativeFile(Project project) {
        return resolveNativePath(project).toFile();
    }
}
