using System;
using System.Runtime.InteropServices;

namespace ViewerLib
{
    public static class Converter
    {
        // DOCX → PNG
        [DllExport("ConvertDocxToPng", CallingConvention = CallingConvention.StdCall)]
        public static int ConvertDocxToPng(
            [MarshalAs(UnmanagedType.LPWStr)] string inputPath,
            [MarshalAs(UnmanagedType.LPWStr)] string outputDir)
        {
            return DocxConverter.Convert(inputPath, outputDir);
        }

        // PPTX → PNG
        [DllExport("ConvertPptxToPng", CallingConvention = CallingConvention.StdCall)]
        public static int ConvertPptxToPng(
            [MarshalAs(UnmanagedType.LPWStr)] string inputPath,
            [MarshalAs(UnmanagedType.LPWStr)] string outputDir)
        {
            return PptxConverter.Convert(inputPath, outputDir);
        }

        // XLSX → PNG
        [DllExport("ConvertXlsxToPng", CallingConvention = CallingConvention.StdCall)]
        public static int ConvertXlsxToPng(
            [MarshalAs(UnmanagedType.LPWStr)] string inputPath,
            [MarshalAs(UnmanagedType.LPWStr)] string outputDir)
        {
            return XlsxConverter.Convert(inputPath, outputDir);
        }
    }
}