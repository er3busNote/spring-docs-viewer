using Microsoft.Net.Http.Headers;

namespace Common.Utils
{
    public static class FileTypeUtil
    {
        private static readonly MediaTypeHeaderValue Pdf = MediaTypeHeaderValue.Parse("application/pdf");
        private static readonly MediaTypeHeaderValue Docx = MediaTypeHeaderValue.Parse("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        private static readonly MediaTypeHeaderValue Pptx = MediaTypeHeaderValue.Parse("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        private static readonly MediaTypeHeaderValue Xlsx = MediaTypeHeaderValue.Parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        private static readonly MediaTypeHeaderValue Jpeg = MediaTypeHeaderValue.Parse("image/jpeg");
        private static readonly MediaTypeHeaderValue Png = MediaTypeHeaderValue.Parse("image/png");

        /* =========================================
         * Allow / Not Allow
         * ========================================= */

        public static bool IsNotAllowType(string mimeType) => !IsAllowType(mimeType);

        public static bool IsAllowType(string mimeType)
        {
            var type = MediaTypeHeaderValue.Parse(mimeType);

            return new[]
            {
                Pdf, Docx, Pptx, Xlsx, Jpeg, Png
            }.Any(allowed => Includes(allowed, type));
        }

        /* =========================================
         * Type Checks
         * ========================================= */

        public static bool IsPdf(string contentType) => Includes(Pdf, MediaTypeHeaderValue.Parse(contentType));

        public static bool IsDocx(string contentType) => Includes(Docx, MediaTypeHeaderValue.Parse(contentType));

        public static bool IsPptx(string contentType) => Includes(Pptx, MediaTypeHeaderValue.Parse(contentType));

        public static bool IsXlsx(string contentType) => Includes(Xlsx, MediaTypeHeaderValue.Parse(contentType));

        /* =========================================
         * MediaType includes 구현
         * ========================================= */

        private static bool Includes(MediaTypeHeaderValue parent, MediaTypeHeaderValue child)
        {
            return parent.Type.Equals(child.Type, StringComparison.OrdinalIgnoreCase)
                   && (parent.SubType == "*"
                       || parent.SubType.Equals(child.SubType, StringComparison.OrdinalIgnoreCase));
        }
    }
}