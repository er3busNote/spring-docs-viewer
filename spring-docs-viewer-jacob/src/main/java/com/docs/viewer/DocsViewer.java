package com.docs.viewer;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

public class DocsViewer {

    public static void main(String[] args) {
        String input = "C:\\test.docx";
        String output = "C:\\test.pdf";

        ActiveXComponent converter = new ActiveXComponent("DocsViewerInterop.OfficeConverter");
        try {
            Dispatch.call(converter, "ConvertWordToPdf", input, output);
            System.out.println("✅ Converted: " + output);
        } finally {
            converter.safeRelease();
        }
    }
}
