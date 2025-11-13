using System;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using SkiaSharp;

public static class ViewerLib
{
    [UnmanagedCallersOnly(EntryPoint = "ConvertDocxToPng")]
    public static IntPtr ConvertDocxToPng(IntPtr inputPtr, IntPtr outputPtr)
    {
        try
        {
            string inputPath = Marshal.PtrToStringUTF8(inputPtr);
            string outputPath = Marshal.PtrToStringUTF8(outputPtr);

            ConvertDocxInternal(inputPath, outputPath);

            string msg = "OK";
            byte[] utf8 = Encoding.UTF8.GetBytes(msg + '\0');
            IntPtr resultPtr = Marshal.AllocCoTaskMem(utf8.Length);
            Marshal.Copy(utf8, 0, resultPtr, utf8.Length);
            return resultPtr;
        }
        catch (Exception ex)
        {
            string err = "ERR:" + ex.Message;
            byte[] utf8 = Encoding.UTF8.GetBytes(err + '\0');
            IntPtr resultPtr = Marshal.AllocCoTaskMem(utf8.Length);
            Marshal.Copy(utf8, 0, resultPtr, utf8.Length);
            return resultPtr;
        }
    }

    private static void ConvertDocxInternal(string docxPath, string pngPath)
    {
        using var doc = WordprocessingDocument.Open(docxPath, false);
        var body = doc.MainDocumentPart.Document.Body;
        int width = 1200, height = 1600;
        using var bitmap = new SKBitmap(width, height);
        using var canvas = new SKCanvas(bitmap);
        canvas.Clear(SKColors.White);

        int y = 60;
        var paint = new SKPaint { TextSize = 20, IsAntialias = true, Color = SKColors.Black };

        foreach (var p in body.Elements<Paragraph>())
        {
            var text = string.Join("", p.Descendants<Text>().Select(t => t.Text));
            canvas.DrawText(text, 40, y, paint);
            y += 26;
            if (y > height - 100) break;
        }

        using var image = SKImage.FromBitmap(bitmap);
        using var data = image.Encode(SKEncodedImageFormat.Png, 100);
        File.WriteAllBytes(pngPath, data.ToArray());
    }
}