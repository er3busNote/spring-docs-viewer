using DocumentFormat.OpenXml.Packaging;
using SkiaSharp;
using System.IO;

namespace ViewerLib
{
    public class PptxConverter
    {
        public static int Convert(string inputPath, string outputDir)
        {
            using var ppt = PresentationDocument.Open(inputPath, false);
            var slides = ppt.PresentationPart.SlideParts;

            int index = 1;

            foreach (var slide in slides)
            {
                string text = slide.Slide.InnerText;

                string outFile = Path.Combine(outputDir, $"slide_{index}.png");

                using var bitmap = new SKBitmap(1280, 720);
                using var canvas = new SKCanvas(bitmap);

                canvas.Clear(SKColors.White);

                using var paint = new SKPaint
                {
                    Color = SKColors.Black,
                    TextSize = 30
                };

                canvas.DrawText(text, 50, 100, paint);

                using var image = SKImage.FromBitmap(bitmap);
                using var data = image.Encode(SKEncodedImageFormat.Png, 100);
                File.WriteAllBytes(outFile, data.ToArray());

                index++;
            }

            return index - 1;
        }
    }
}