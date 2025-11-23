using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using NHibernate.Tool.hbm2ddl;

namespace DB.Model
{
    public static class DBHelper
    {
        public static NHibernate.Cfg.Configuration ConfigureNHibernate()
        {
            // appsettings.json 읽기
            var configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json")
                .Build();

            var connectionString = configuration.GetConnectionString("DefaultConnection");
            var ddlOption = configuration["NHibernate:Hbm2DdlAuto"]?.ToLower();

            // FluentNHibernate Configuration
            var cfg = Fluently.Configure()
                .Database(MySQLConfiguration.Standard.ConnectionString(connectionString))
                .Mappings(m => m.FluentMappings.AddFromAssemblyOf<UserInfoBaseMap>())
                .BuildConfiguration();

            // Hbm2DdlAuto 처리
            switch (ddlOption)
            {
                case "create":
                    new SchemaExport(cfg).Create(true, true);
                    break;
                case "update":
                    new SchemaUpdate(cfg).Execute(true, true);
                    break;
                case "none":
                default:
                    // 아무 작업 안함
                    break;
            }

            return cfg;
        }
    }
}