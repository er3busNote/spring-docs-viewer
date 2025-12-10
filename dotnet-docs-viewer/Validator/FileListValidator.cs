using System.ComponentModel.DataAnnotations;

namespace WebApp.Validator
{
    [AttributeUsage(AttributeTargets.Property | AttributeTargets.Parameter)]
    public class ValidFileListAttribute : ValidationAttribute
    {
        private static readonly List<string> AllowedContentTypes = new()
        {
            "image/jpeg",
            "image/png"
        };

        private const long MaxFileSize = 5 * 1024 * 1024; // 5MB

        protected override ValidationResult IsValid(object value, ValidationContext validationContext)
        {
            if (value is not List<IFormFile> files || files.Count == 0)
            {
                return new ValidationResult("파일이 비어있습니다.");
            }

            foreach (var file in files)
            {
                if (file == null || file.Length == 0)
                {
                    return new ValidationResult("비어있는 파일은 허용되지 않습니다.");
                }

                if (file.Length > MaxFileSize)
                {
                    return new ValidationResult("파일 크기가 5MB를 초과했습니다.");
                }

                if (!AllowedContentTypes.Contains(file.ContentType))
                {
                    return new ValidationResult($"허용되지 않는 파일 형식입니다. ({file.ContentType})");
                }
            }

            return ValidationResult.Success;
        }
    }
}