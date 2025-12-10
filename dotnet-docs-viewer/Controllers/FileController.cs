using System.Net.Mime;
using Common.Utils;
using HeyRed.Mime;
using Microsoft.AspNetCore.Mvc;
using WebApp.Common.Dto;
using WebApp.Common.Error;
using WebApp.Common.Error.Types;
using WebApp.Dto;
using WebApp.Service;

namespace WebApp.Controllers
{
    [ApiController]
    [Route("file")]
    public class FileController : ControllerBase
    {
        private readonly IFileService _fileService;
        private readonly IPreviewService _previewService;
        private readonly IErrorHandler _errorHandler;

        public FileController(IFileService fileService, IPreviewService previewService, IErrorHandler errorHandler)
        {
            _fileService = fileService;
            _previewService = previewService;
            _errorHandler = errorHandler;
        }

        [HttpGet]
        public IActionResult FileImage([FromQuery] int attachFile)
        {
            try
            {
                var fileResponse = FindFileAsync(attachFile).Result;;
                var mimeType = fileResponse.MimeType;

                if (FileTypeUtil.IsAllowType(mimeType))
                {
                    byte[] resource = _previewService.FindFileAsync(fileResponse).Result;

                    return File(resource, mimeType);
                }

                return BadRequest(ResponseHandler.Error(_errorHandler.BuildError(
                    ErrorCode.FILETYPE_MAPPING_INVALID,
                    new ErrorInfo { Errors = mimeType })
                ));
            }
            catch (Exception ex)
            {
                return UnprocessableEntity(_errorHandler.BuildError(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    new ErrorInfo { Message = ex.Message }
                ));
            }
        }

        [HttpPost("upload")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> UploadFiles([FromForm] FileUploadRequest request)
        {
            try
            {
                var files = request.Files;
                var fileAttachInfos = await _fileService.SaveFileAsync(files);

                foreach (var fileAttachInfo in fileAttachInfos)
                {
                    var attachFile = fileAttachInfo.FileAttachCode;
                    var fileResponse = FindFileAsync(attachFile).Result;

                    await _previewService.SaveFileAsync(attachFile, fileResponse);
                }

                return Created("/create", ResponseHandler.Success(
                    StatusCodes.Status201Created, 
                    "파일이 업로드 되었습니다"
                ));
            }
            catch (Exception ex)
            {
                var error = _errorHandler.BuildError(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    new ErrorInfo { Message = ex.Message });

                return UnprocessableEntity(error);
            }
        }

        private async Task<FileResponse> FindFileAsync(int attachFile)
        {
            byte[] fileBytes = await _fileService.FindFileAsync(attachFile);
            string mimeType = FindMimeType(fileBytes);

            return FileResponse.Of(fileBytes, mimeType);
        }

        private string FindMimeType(byte[] fileBytes)
        {
            try
            {
                return MimeGuesser.GuessMimeType(fileBytes);
            }
            catch
            {
                return MediaTypeNames.Application.Octet;
            }
        }
    }
}