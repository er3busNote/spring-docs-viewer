using System.Transactions;
using Common.Utils;
using DB.Model;
using Microsoft.AspNetCore.StaticFiles;
using Microsoft.Extensions.Options;
using WebApp.Dto;
using WebApp.Repository;
using WebApp.Types;

namespace WebApp.Service
{
    public interface IPreviewService
    {
        Task<byte[]> FindFileAsync(FileResponse fileResponse);
        Task SaveFileAsync(int attachFile, byte[] image, string targetFolder, int index);
        Task SaveFileAsync(int attachFile, FileResponse fileResponse);
    }
    
    public class PreviewService
    {
        private readonly IRepository<FileInfoBaseModel> _fileRepository;
        private readonly IRepository<PreviewInfoBaseModel> _previewRepository;
        private readonly IOptions<FileSetting> _fileSetting;

        private const string FileDirectory = "cmmn";

        public PreviewService(IRepository<FileInfoBaseModel> fileRepository, IRepository<PreviewInfoBaseModel> previewRepository, IOptions<FileSetting> fileSetting)
        {
            _fileRepository = fileRepository;
            _previewRepository = previewRepository;
            _fileSetting = fileSetting;
        }

        // ───────────────────────────────────────────────────────────
        // 파일 변환 후 바로 반환하는 기능
        // ───────────────────────────────────────────────────────────
        public async Task<byte[]> FindFileAsync(FileResponse fileResponse)
        {
            byte[] resource = fileResponse.Resource;
            string mimeType = fileResponse.MimeType;

            var previewType = GetPreviewType(mimeType);

            return previewType switch
            {
                PreviewType.PPTX => await ConvertPptxToImage(resource),
                PreviewType.XLSX => await ConvertXlsxToImage(resource),
                PreviewType.PDF => await ConvertPdfToImage(resource),
                _ => resource
            };
        }

        // ---------------------------------------------------------------------
        // 파일 변환 후 이미지 여러 개 생성하여 DB에 저장하는 로직
        // ---------------------------------------------------------------------
        public async Task SaveFileAsync(int attachFile, byte[] image, string targetFolder, int index)
        {
            using var scope = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            var fileInfo = await FindFileByIdAsync(attachFile);

            string rootPath = _fileSetting.Value.ImagePath;
            string uploadDirectory = FileUtil.GetDirectory(rootPath, targetFolder);
            string uploadPath = FileUtil.GetFilePath(uploadDirectory);

            string mimeType = FindMimeType(image);
            int fileSize = image.Length;

            var previewInfo = PreviewInfoModel.Of(fileInfo, uploadPath, mimeType, fileSize);

            await _previewRepository.SaveAsync(previewInfo);

            // 실제 파일 저장 (암호화)
            CryptoUtil.EncryptFile(image, FileUtil.GetTargetFile(uploadPath));

            scope.Complete();
        }

        // Overload (Docx/Pdf/Xlsx/Pptx → 여러 개 pages 저장)
        public async Task SaveFileAsync(int attachFile, FileResponse fileResponse)
        {
            byte[] resource = fileResponse.Resource;
            string mimeType = fileResponse.MimeType;

            var previewType = GetPreviewType(mimeType);

            switch (previewType)
            {
                case PreviewType.PPTX:
                    await CreatePptxToImage(attachFile, resource);
                    break;
                case PreviewType.XLSX:
                    await CreateXlsxToImage(attachFile, resource);
                    break;
                case PreviewType.PDF:
                    await CreatePdfToImage(attachFile, resource);
                    break;
            }
        }

        // ───────────────────────────────────────────────────────────
        // 유틸 메서드
        // ───────────────────────────────────────────────────────────
        private async Task<FileInfoBaseModel> FindFileByIdAsync(int id)
        {
            var file = await _fileRepository.GetByIdAsync(id);

            return file ?? throw new FileNotFoundException("존재하지 않는 파일입니다.");
        }

        private string FindMimeType(byte[] bytes)
        {
            new FileExtensionContentTypeProvider().TryGetContentType("file", out var mime);
            return mime ?? "application/octet-stream";
        }

        private PreviewType GetPreviewType(string mimeType)
        {
            if (FileTypeUtil.IsPptx(mimeType)) return PreviewType.PPTX;
            if (FileTypeUtil.IsXlsx(mimeType)) return PreviewType.XLSX;
            if (FileTypeUtil.IsDocx(mimeType)) return PreviewType.DOCX;
            if (FileTypeUtil.IsPdf(mimeType)) return PreviewType.PDF;

            return PreviewType.NONE;
        }

        // ───────────────────────────────────────────────────────────
        // 변환 기능 (DocumentUtil 사용)
        // ───────────────────────────────────────────────────────────
        private async Task<byte[]> ConvertPdfToImage(byte[] pdf)
        {
            var images = await DocumentUtil.ConvertPdfToPng(pdf);
            return DocumentUtil.MergeImagesVertically(images);
        }

        private async Task CreatePdfToImage(int attachFile, byte[] pdf)
        {
            var images = await DocumentUtil.ConvertPdfToPng(pdf);
            int index = 1;

            foreach (var img in images)
            {
                await SaveFileAsync(attachFile, img, FileDirectory, index++);
            }
        }

        private async Task<byte[]> ConvertPptxToImage(byte[] pptx)
        {
            var images = await DocumentUtil.ConvertPptxToPng(pptx);
            return DocumentUtil.MergeImagesVertically(images);
        }

        private async Task CreatePptxToImage(int attachFile, byte[] pptx)
        {
            var images = await DocumentUtil.ConvertPptxToPng(pptx);
            int index = 1;

            foreach (var img in images)
            {
                await SaveFileAsync(attachFile, img, FileDirectory, index++);
            }
        }

        private async Task<byte[]> ConvertXlsxToImage(byte[] xlsx)
        {
            var images = await DocumentUtil.ConvertXlsxToPng(xlsx);
            return DocumentUtil.MergeImagesVertically(images);
        }

        private async Task CreateXlsxToImage(int attachFile, byte[] xlsx)
        {
            var images = await DocumentUtil.ConvertXlsxToPng(xlsx);
            int index = 1;

            foreach (var img in images)
            {
                await SaveFileAsync(attachFile, img, FileDirectory, index++);
            }
        }
    }
}