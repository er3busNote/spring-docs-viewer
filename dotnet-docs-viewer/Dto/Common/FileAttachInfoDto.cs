using Newtonsoft.Json;

namespace WebApp.Dto
{
    [JsonObject]
    public abstract class FileAttachInfoBaseDto : BaseDto
    {
        public virtual int FileAttachCode { get; protected set; }
        public virtual string FileName { get; protected set; } = null!;
        public virtual string FileType { get; protected set; } = null!;
        public virtual int? FileSize { get; protected set; }
        public virtual string FileExtension { get; protected set; } = null!;
    }

    public class FileAttachInfoDto : FileAttachInfoBaseDto
    {
        public static FileAttachInfoDto Of(FileInfoDto fileInfo)
        {
            return new FileAttachInfoDto
            {
                FileAttachCode = fileInfo.FileCode,
                FileName = fileInfo.FileName,
                FileType = fileInfo.FileType,
                FileSize = fileInfo.FileSize,
                FileExtension = fileInfo.FileExtension
            };
        }
    }
}