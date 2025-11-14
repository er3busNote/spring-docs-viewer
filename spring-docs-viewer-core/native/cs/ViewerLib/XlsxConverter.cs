using DocumentFormat.OpenXml.Packaging;
using SkiaSharp;
using System.IO;

namespace ViewerLib
{
    public class XlsxConverter
    {
        public static int Convert(string inputPath, string outputDir)
        {
            using var doc = SpreadsheetDocument.Open(inputPath, false);

            var sheet = doc.WorkbookPart.WorksheetParts.First();
            var cells = sheet.Worksheet.Descendants<DocumentFormat.OpenXml.Spreadsheet.Cell>();

            int index = 1;

            string outFile = Path.Combine(outputDir, $"sheet_{index}.png");

            using var bitmap = new SKBitmap(1200, 800);
            using var canvas = new SKCanvas(bitmap);

            canvas.Clear(SKColors.White);

            using var paint = new SKPaint
            {
                Color = SKColors.Black,
                TextSize = 20
            };

            int y = 40;

            foreach (var c in cells)
            {
                canvas.DrawText(c.CellReference + ": " + c.InnerText, 20, y, paint);
                y += 30;
            }

            using var image = SKImage.FromBitmap(bitmap);
            using var data = image.Encode(SKEncodedImageFormat.Png, 100);
            File.WriteAllBytes(outFile, data.ToArray());

            return 1;
        }
    }
}