using FluentNHibernate.Mapping;
using Newtonsoft.Json;

namespace DB.Model
{
    [JsonObject]
    public abstract class PreviewInfoBaseModel : BaseModel
    {
        public virtual long PreviewId { get; set; }

        // ManyToOne (LAZY)
        public virtual FileInfoBaseModel File { get; set; }

        public virtual string FilePath { get; set; }
        public virtual string FileType { get; set; }
        public virtual int? FileSize { get; set; }

        public virtual string Register { get; set; }
        public virtual DateTime RegisterDate { get; set; } = DateTime.Now;

        public virtual string Updater { get; set; }
        public virtual DateTime UpdateDate { get; set; } = DateTime.Now;
    }
    
    public class PreviewInfoBaseMap : ClassMap<PreviewInfoBaseModel>
    {
        public PreviewInfoBaseMap()
        {
            // 테이블명
            Table("PREVIEW");

            // PK (AUTO_INCREMENT)
            Id(x => x.PreviewId)
                .Column("PREVIEW_ID")
                .GeneratedBy.Identity();

            // ManyToOne → File
            References(x => x.File)
                .Column("FILE_CD")
                .Not.Nullable()
                .LazyLoad()
                .ForeignKey("FK_CMM_FILE_TO_PREVIEW")
                .ReadOnly();   // updatable = false

            // 일반 컬럼
            Map(x => x.FilePath)
                .Column("FILE_PATH")
                .Length(200);

            Map(x => x.FileType)
                .Column("FILE_TYPE")
                .Length(100);

            Map(x => x.FileSize)
                .Column("FILE_SIZE");

            Map(x => x.Register)
                .Column("REGR")
                .Length(30)
                .Not.Nullable();

            // @CreationTimestamp 대체
            Map(x => x.RegisterDate)
                .Column("REG_DT")
                .Not.Nullable()
                .Default("current_timestamp()");

            Map(x => x.Updater)
                .Column("UPDR")
                .Length(30)
                .Not.Nullable();

            // @UpdateTimestamp 대체
            Map(x => x.UpdateDate)
                .Column("UPD_DT")
                .Not.Nullable()
                .Default("current_timestamp()")
                .CustomSqlType("TIMESTAMP")
                .Generated.Always();
        }
    }
    
    public class PreviewInfoModel : PreviewInfoBaseModel
    {
        public static PreviewInfoModel Of(FileInfoBaseModel fileInfo, string filePath, string fileType, int fileSize)
        {
            return new PreviewInfoModel
            {
                File = fileInfo,
                FilePath = filePath,
                FileType = fileType,
                FileSize = fileSize,
                Register = "M000000002",
                Updater = "M000000002"
            };
        }
    }
}