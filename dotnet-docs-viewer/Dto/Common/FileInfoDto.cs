using Common.Utils;
using DB.Model;
using Newtonsoft.Json;

namespace WebApp.Dto
{
    [JsonObject]
    public abstract class FileInfoBaseDto : BaseDto
    {
        public virtual long FileCode { get; protected set; }
        public virtual string FileName { get; protected set; } = null!;
        public virtual string FilePath { get; protected set; } = null!;
        public virtual string FileType { get; protected set; } = null!;
        public virtual int? FileSize { get; protected set; }
        public virtual string FileExtension { get; protected set; } = null!;
        public virtual string FileNameWithoutExtension { get; protected set; } = null!;
    }
    
    public class FileInfoDto : FileInfoBaseDto
    {
        public static FileInfoDto Of(FileInfoBaseModel fileInfo)
        {
            return new FileInfoDto
            {
                FileCode = fileInfo.FileCode,
                FileName = fileInfo.FileName,
                FilePath = fileInfo.FilePath,
                FileType = fileInfo.FileType,
                FileSize = fileInfo.FileSize,
                FileExtension = FileUtil.GetFileExtension(fileInfo.FileName),
                FileNameWithoutExtension = FileUtil.RemoveFileExtension(fileInfo.FileName)
            };
        }
    }
}