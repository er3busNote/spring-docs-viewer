using Common.Utils;
using DB.Model;
using Microsoft.Extensions.Options;
using WebApp.Dto;
using WebApp.Repository;

namespace WebApp.Service
{
    public interface IFileService
    {
        Task<byte[]> FindFileAsync(int attachFile);
        Task<List<FileAttachInfoDto>> SaveFileAsync(List<IFormFile> files);
    }
    
    public class FileService : IFileService
    {
        private static readonly string FileDirectory = "cmmn";
            
        private readonly IRepository<FileInfoBaseModel> _fileRepository;
        private readonly IOptions<FileSetting> _fileSetting;

        public FileService(IRepository<FileInfoBaseModel> fileRepository, IOptions<FileSetting> fileSetting)
        {
            _fileRepository = fileRepository;
            _fileSetting = fileSetting;
        }
        
        public async Task<byte[]> FindFileAsync(int attachFile)
        {
            var file = await _fileRepository.GetByIdAsync(attachFile);

            if (file == null)
                throw new FileNotFoundException("해당되는 파일을 찾을 수 없습니다.");

            if (file.CloudYn == 'N')
            {
                var rootPath = _fileSetting.Value.FilePath;
                var filePath = file.FilePath.Replace("..", rootPath);

                return CryptoUtil.DecryptFile(filePath);
            }

            throw new InvalidOperationException("Cloud 파일은 아직 지원하지 않습니다.");
        }
        
        public async Task<List<FileAttachInfoDto>> SaveFileAsync(List<IFormFile> files)
        {
            var result = new List<FileAttachInfoDto>();

            foreach (var file in files)
            {
                var info = await SaveFileAsync(file, FileDirectory);
                result.Add(FileAttachInfoDto.Of(info));
            }

            return result;
        }
        
        private async Task<FileInfoDto> SaveFileAsync(IFormFile file, string targetFolder)
        {
            var rootPath = _fileSetting.Value.FilePath;

            var uploadDirectory = FileUtil.GetUploadDirectory(
                FileUtil.GetDirectory(rootPath, targetFolder)
            );

            var filePath = FileUtil.GetFilePath(uploadDirectory);

            var fileInfo = FileInfoModel.Of(
                file.FileName,
                filePath,
                file.ContentType,
                (int)file.Length,
                'N'
            );

            await _fileRepository.SaveAsync(fileInfo);

            var fileBytes = await ToByteArrayAsync(file);
            CryptoUtil.EncryptFile(fileBytes, FileUtil.GetTargetFile(fileInfo.FilePath));

            return FileInfoDto.Of(fileInfo);
        }
        
        private static async Task<byte[]> ToByteArrayAsync(IFormFile file)
        {
            await using var ms = new MemoryStream();
            await file.CopyToAsync(ms);
            return ms.ToArray();
        }
    }
}