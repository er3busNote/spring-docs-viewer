using DocumentFormat.OpenXml.Packaging;
using SkiaSharp;
using System.IO;

namespace ViewerLib
{
    public class DocxConverter
    {
        public static int Convert(string inputPath, string outputDir)
        {
            using var doc = WordprocessingDocument.Open(inputPath, false);

            int page = 1;

            // 텍스트 기반 매우 간단화된 샘플 (실제 구현은 마크업 렌더링 필요)
            string text = doc.MainDocumentPart.Document.Body.InnerText;

            string outFile = Path.Combine(outputDir, $"page_{page}.png");
            using var bitmap = new SKBitmap(800, 1200);
            using var canvas = new SKCanvas(bitmap);

            canvas.Clear(SKColors.White);

            using var paint = new SKPaint
            {
                Color = SKColors.Black,
                TextSize = 24
            };

            canvas.DrawText(text, 20, 40, paint);

            using var image = SKImage.FromBitmap(bitmap);
            using var data = image.Encode(SKEncodedImageFormat.Png, 100);
            File.WriteAllBytes(outFile, data.ToArray());

            return 1; // page count
        }
    }
}