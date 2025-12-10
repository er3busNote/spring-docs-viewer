using Newtonsoft.Json;
using FluentNHibernate.Mapping;

namespace DB.Model
{
    [JsonObject]
    public abstract class FileInfoBaseModel : BaseModel
    {
        public virtual int FileCode { get; set; }
        public virtual string FileName { get; set; }
        public virtual string FilePath { get; set; }
        public virtual string FileType { get; set; }
        public virtual int? FileSize { get; set; }
        public virtual char CloudYn { get; set; } = 'N';
        public virtual string Register { get; set; }
        public virtual DateTime RegisterDate { get; set; } = DateTime.Now;
        public virtual string Updater { get; set; }
        public virtual DateTime UpdateDate { get; set; } = DateTime.Now;
    }
    
    public class FileInfoBaseMap : ClassMap<FileInfoBaseModel>
    {
        public FileInfoBaseMap()
        {
            // 테이블명
            Table("CMM_FILE");

            // Primary Key (Sequence Generator)
            Id(x => x.FileCode)
                .Column("FILE_CD")
                .GeneratedBy.HiLo("CMM_FILE_SEQUENCE", "next_val", "100");

            // 일반 필드 매핑
            Map(x => x.FileName).Column("FILE_NM").Length(100);
            Map(x => x.FilePath).Column("FILE_PATH").Length(200);
            Map(x => x.FileType).Column("FILE_TYPE").Length(100);
            Map(x => x.FileSize).Column("FILE_SIZE");
            Map(x => x.CloudYn).Column("CLOUD_YN");

            Map(x => x.Register).Column("REGR").Length(30).Not.Nullable();
            Map(x => x.RegisterDate)
                .Column("REG_DT")
                .Not.Nullable()
                .Default("current_timestamp()");

            Map(x => x.Updater)
                .Column("UPDR")
                .Length(30)
                .Not.Nullable();

            // UpdateTimestamp
            Map(x => x.UpdateDate)
                .Column("UPD_DT")
                .Not.Nullable()
                .Default("current_timestamp()")  
                .CustomSqlType("TIMESTAMP")
                .Generated.Always(); // UPDATE 시 자동 변경
        }
    }
    
    public class FileInfoModel : FileInfoBaseModel
    {
        public static FileInfoModel Of(string fileName, string filePath, string fileType, int fileSize, char cloudYn)
        {
            return new FileInfoModel
            {
                FileName = fileName,
                FilePath = filePath,
                FileType = fileType,
                FileSize = fileSize,
                CloudYn = cloudYn,
                Register = "M000000002",
                Updater = "M000000002"
            };
        }
    }
}