using System.ComponentModel.DataAnnotations;
using WebApp.Validator;

namespace WebApp.Dto
{
    public class FileUploadRequest
    {
        [Required]
        [ValidFileList]   // Custom validator 적용
        public List<IFormFile> Files { get; set; }
    }
}