using System;
using System.IO;
using System.Runtime.InteropServices;
using Word = Microsoft.Office.Interop.Word;
using Excel = Microsoft.Office.Interop.Excel;
using PowerPoint = Microsoft.Office.Interop.PowerPoint;

namespace DocsViewerInterop
{
    [ComVisible(true)]
    [Guid("F128B180-89D4-47C5-8DB0-9A392A49A6E3")]
    [ClassInterface(ClassInterfaceType.AutoDual)]
    public class OfficeConverter
    {
        // Word → PDF
        public string ConvertWordToPdf(string inputPath, string outputPath)
        {
            var app = new Word.Application();
            try
            {
                var doc = app.Documents.Open(inputPath);
                doc.SaveAs2(outputPath, Word.WdSaveFormat.wdFormatPDF);
                doc.Close();
                return outputPath;
            }
            finally
            {
                app.Quit();
            }
        }

        // Excel → PDF
        public string ConvertExcelToPdf(string inputPath, string outputPath)
        {
            var app = new Excel.Application();
            try
            {
                var workbook = app.Workbooks.Open(inputPath);
                workbook.ExportAsFixedFormat(Excel.XlFixedFormatType.xlTypePDF, outputPath);
                workbook.Close();
                return outputPath;
            }
            finally
            {
                app.Quit();
            }
        }

        // PowerPoint → PDF
        public string ConvertPptToPdf(string inputPath, string outputPath)
        {
            var app = new PowerPoint.Application();
            try
            {
                var presentation = app.Presentations.Open(inputPath, WithWindow: Microsoft.Office.Core.MsoTriState.msoFalse);
                presentation.SaveAs(outputPath, PowerPoint.PpSaveAsFileType.ppSaveAsPDF);
                presentation.Close();
                return outputPath;
            }
            finally
            {
                app.Quit();
            }
        }
    }
}