package com.docs.viewer;

import java.io.File;

/**
 * Gradle DSL extension for configuring the DocsViewer plugin.
 *
 * Example usage:
 * docsViewer {
 *     enableCs = true
 *     enableGo = false
 *     outputDir = file("$buildDir/native")
 * }
 */
public class ViewerExtension {
    public boolean enableCs = true;
    public boolean enableGo = true;
    public boolean enableJni = true;
    public File outputDir;

    public boolean isEnableCs() { return enableCs; }
    public void setEnableCs(boolean enableCs) { this.enableCs = enableCs; }

    public boolean isEnableGo() { return enableGo; }
    public void setEnableGo(boolean enableGo) { this.enableGo = enableGo; }

    public boolean isEnableJni() { return enableJni; }
    public void setEnableJni(boolean enableJni) { this.enableJni = enableJni; }

    public File getOutputDir() { return outputDir; }
    public void setOutputDir(File outputDir) { this.outputDir = outputDir; }
}
