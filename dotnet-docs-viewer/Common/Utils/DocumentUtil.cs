using UglyToad.PdfPig;
using UglyToad.PdfPig.Rendering.Skia;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Presentation;
using DocumentFormat.OpenXml.Spreadsheet;
using SkiaSharp;

namespace Common.Utils
{
    public static class DocumentUtil
    {
        // ======================================================
        // 1. PDF → PNG 리스트
        // ======================================================
        public static List<byte[]> ConvertPdfToPng(byte[] pdfBytes, float scale = 2.0f)
        {
            using var ms = new MemoryStream(pdfBytes);

            // Skia 렌더링 옵션을 사용해서 문서 열기
            using var document = PdfDocument.Open(ms, SkiaRenderingParsingOptions.Instance);

            // 페이지 팩토리 등록 (확실히 호출)
            document.AddSkiaPageFactory();

            var results = new List<byte[]>();

            // PdfPig 페이지는 1-based 인덱스
            for (int p = 1; p <= document.NumberOfPages; p++)
            {
                // SKBitmap을 얻음 (문서/패키지 버전에 따라 제네릭 타입이 SKBitmap 또는 SKPicture 일 수 있음)
                // GetPage<T> 사용 가능: 예) GetPage<SKBitmap>(p)
                var skBitmap = document.GetPage<SKBitmap>(p);

                // 필요시 스케일을 적용 (GetPage로 이미 원하는 크기라면 스케일 불필요)
                if (scale != 1.0f)
                {
                    int newW = (int)(skBitmap.Width * scale);
                    int newH = (int)(skBitmap.Height * scale);
                    using var resized = skBitmap.Resize(new SKImageInfo(newW, newH), SKFilterQuality.High);
                    using var image = SKImage.FromBitmap(resized ?? skBitmap);
                    using var data = image.Encode(SKEncodedImageFormat.Png, 100);
                    results.Add(data.ToArray());
                }
                else
                {
                    using var image = SKImage.FromBitmap(skBitmap);
                    using var data = image.Encode(SKEncodedImageFormat.Png, 100);
                    results.Add(data.ToArray());
                }

                // skBitmap/objects disposed by using / GC
            }

            return results;
        }

        // ======================================================
        // 2. PPTX → PNG 리스트
        // ======================================================
        public static List<byte[]> ConvertPptxToPng(byte[] pptxBytes, int slideWidth = 1280, int slideHeight = 720)
        {
            var result = new List<byte[]>();

            using var ms = new MemoryStream(pptxBytes);
            using var presentation = PresentationDocument.Open(ms, false);
            var presentationPart = presentation.PresentationPart;
            if (presentationPart == null) return result;

            var slideIds = presentationPart.Presentation.SlideIdList?.ChildElements;
            if (slideIds == null) return result;

            foreach (var sId in slideIds.OfType<SlideId>())
            {
                var relId = sId.RelationshipId;
                if (string.IsNullOrEmpty(relId)) continue;

                var slidePart = (SlidePart) presentationPart.GetPartById(relId);

                // Collect text strings from slide (simple approach)
                var texts = slidePart.Slide.Descendants<DocumentFormat.OpenXml.Drawing.Text>()
                                .Select(t => t.Text)
                                .Where(t => !string.IsNullOrWhiteSpace(t))
                                .ToList();

                // Create SKBitmap and draw
                using var bitmap = new SKBitmap(slideWidth, slideHeight, true);
                using var canvas = new SKCanvas(bitmap);
                canvas.Clear(SKColors.White);

                // Paint settings
                using var paint = new SKPaint
                {
                    Color = SKColors.Black,
                    IsAntialias = true,
                    Typeface = SKTypeface.FromFamilyName("Arial"),
                    TextSize = 22
                };

                // Draw each text line with simple layout
                var margin = 40;
                var y = margin;
                var lineHeight = (int)(paint.TextSize + 10);

                foreach (var text in texts)
                {
                    // multi-line handling
                    var lines = WrapText(text, paint, slideWidth - margin * 2);
                    foreach (var line in lines)
                    {
                        canvas.DrawText(line, margin, y + paint.TextSize, paint);
                        y += lineHeight;
                        if (y > slideHeight - margin) break;
                    }
                    if (y > slideHeight - margin) break;
                }

                // convert SKBitmap to PNG bytes
                using var image = SKImage.FromBitmap(bitmap);
                using var data = image.Encode(SKEncodedImageFormat.Png, 100);
                result.Add(data.ToArray());
            }

            return result;
        }

        // ======================================================
        // 3. XLSX → PNG 리스트
        // ======================================================
        public static List<byte[]> ConvertXlsxToPng(byte[] xlsxBytes, int cellWidth = 140, int cellHeight = 40)
        {
            var result = new List<byte[]>();

            using var ms = new MemoryStream(xlsxBytes);
            using var document = SpreadsheetDocument.Open(ms, false);
            var workbookPart = document.WorkbookPart;
            if (workbookPart == null) return result;

            var sheets = workbookPart.Workbook.Sheets?.OfType<Sheet>().ToList();
            if (sheets == null || sheets.Count == 0) return result;

            foreach (var sheet in sheets)
            {
                var worksheetPart = (WorksheetPart) workbookPart.GetPartById(sheet.Id);
                var rows = worksheetPart.Worksheet.Descendants<Row>().ToList();

                int maxRow = rows.Count;
                int maxCol = 0;
                foreach (var row in rows)
                {
                    var cellCount = row.Elements<Cell>().Count();
                    if (cellCount > maxCol) maxCol = cellCount;
                }
                if (maxCol == 0) maxCol = 1;

                int width = maxCol * cellWidth;
                int height = Math.Max(1, maxRow) * cellHeight;

                using var bitmap = new SKBitmap(width, height, true);
                using var canvas = new SKCanvas(bitmap);
                canvas.Clear(SKColors.White);

                // Paint for cell border and text
                using var borderPaint = new SKPaint
                {
                    Color = SKColors.Black,
                    IsAntialias = true,
                    StrokeWidth = 1,
                    Style = SKPaintStyle.Stroke
                };

                using var textPaint = new SKPaint
                {
                    Color = SKColors.Black,
                    IsAntialias = true,
                    Typeface = SKTypeface.FromFamilyName("Arial"),
                    TextSize = 16
                };

                int rowIndex = 0;
                foreach (var row in rows)
                {
                    var cells = row.Elements<Cell>().ToList();
                    for (int c = 0; c < maxCol; c++)
                    {
                        int x = c * cellWidth;
                        int y = rowIndex * cellHeight;

                        // draw cell border
                        var rect = new SKRect(x, y, x + cellWidth, y + cellHeight);
                        canvas.DrawRect(rect, borderPaint);

                        string text = "";
                        if (c < cells.Count)
                        {
                            text = GetCellValue(workbookPart, cells[c]);
                        }

                        if (!string.IsNullOrEmpty(text))
                        {
                            // wrap text to fit cell width
                            var lines = WrapText(text, textPaint, cellWidth - 8);
                            float ty = y + 18;
                            foreach (var line in lines)
                            {
                                canvas.DrawText(line, x + 4, ty, textPaint);
                                ty += textPaint.TextSize + 4;
                                if (ty > y + cellHeight - 4) break;
                            }
                        }
                    }

                    rowIndex++;
                }

                using var image = SKImage.FromBitmap(bitmap);
                using var data = image.Encode(SKEncodedImageFormat.Png, 100);
                result.Add(data.ToArray());
            }

            return result;
        }

        // ======================================================
        // XLSX 셀 값 추출
        // ======================================================
        private static string GetCellValue(WorkbookPart workbookPart, Cell cell)
        {
            var value = cell.CellValue?.InnerText ?? "";

            if (cell.DataType == null) return value;

            switch (cell.DataType?.Value)
            {
                case var dt when dt == CellValues.SharedString:
                    return workbookPart.SharedStringTablePart
                        ?.SharedStringTable
                        ?.Elements<SharedStringItem>()
                        .ElementAtOrDefault(int.Parse(value))
                        ?.InnerText ?? "";

                case var dt when dt == CellValues.Boolean:
                    return value == "1" ? "TRUE" : "FALSE";

                default:
                    return value;
            }
        }

        private static List<string> WrapText(string text, SKPaint paint, int maxWidth)
        {
            var lines = new List<string>();
            if (string.IsNullOrEmpty(text))
            {
                return lines;
            }

            var words = text.Split(new[] { ' ', '\t', '\r', '\n' }, StringSplitOptions.RemoveEmptyEntries);
            var current = "";
            foreach (var w in words)
            {
                var trial = string.IsNullOrEmpty(current) ? w : current + " " + w;
                
                float wlen = paint.MeasureText(trial);
                if (wlen <= maxWidth)
                {
                    current = trial;
                }
                else
                {
                    if (!string.IsNullOrEmpty(current))
                    {
                        lines.Add(current);
                    }
                    // if single word itself larger than width, break word
                    if (paint.MeasureText(w) > maxWidth)
                    {
                        var broken = BreakLongWord(w, paint, maxWidth);
                        foreach (var b in broken)
                        {
                            lines.Add(b);
                        }
                        current = "";
                    }
                    else
                    {
                        current = w;
                    }
                }
            }

            if (!string.IsNullOrEmpty(current))
                lines.Add(current);

            return lines;
        }

        private static IEnumerable<string> BreakLongWord(string word, SKPaint paint, int maxWidth)
        {
            var list = new List<string>();
            var sb = "";
            foreach (var ch in word)
            {
                var t = sb + ch;
                if (paint.MeasureText(t) <= maxWidth)
                {
                    sb = t;
                }
                else
                {
                    if (!string.IsNullOrEmpty(sb))
                    {
                        list.Add(sb);
                        sb = ch.ToString();
                    }
                    else
                    {
                        // single char larger? add anyway to avoid infinite loop
                        list.Add(ch.ToString());
                        sb = "";
                    }
                }
            }
            if (!string.IsNullOrEmpty(sb)) list.Add(sb);
            return list;
        }
    }
}