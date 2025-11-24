using Common.Utils;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using FluentNHibernate.Conventions;
using FluentNHibernate.Conventions.Instances;
using NHibernate.Cfg;
using NHibernate.Tool.hbm2ddl;

namespace DB.Model
{
    public static class DBHelper
    {
        public static Configuration ConfigureNHibernate()
        {
            // appsettings.json 읽기
            var configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json")
                .Build();

            var connectionString = configuration.GetConnectionString("DefaultConnection");
            var ddlOption = configuration["NHibernate:Hbm2DdlAuto"]?.ToLower();

            // NHibernate Configuration 객체 생성
            var nhConfig = Fluently.Configure()
                .Database(MySQLConfiguration.Standard.ConnectionString(connectionString))
                .Mappings(m => m.FluentMappings.AddFromAssemblyOf<UserInfoBaseMap>()
                    .Conventions.Add<UpperSnakeClassConvention>()
                    .Conventions.Add<UpperSnakePropertyConvention>()
                    .Conventions.Add<UpperSnakeIdConvention>()
                ).BuildConfiguration();
            
            // Hbm2DdlAuto 처리
            switch (ddlOption)
            {
                case "create":
                    new SchemaExport(nhConfig).Create(true, true);
                    break;
                case "update":
                    new SchemaUpdate(nhConfig).Execute(true, true);
                    break;
                case "none":
                default:
                    // 아무 작업 안함
                    break;
            }

            return nhConfig;
        }

        // 테이블명 컨벤션
        private class UpperSnakeClassConvention : IClassConvention
        {
            public void Apply(IClassInstance instance)
            {
                // 클래스명 -> 테이블명 변환
                instance.Table(NamingUtils.ToSnakeUpperCase(instance.EntityType.Name));
            }
        }

        // 프로퍼티(컬럼) 컨벤션
        private class UpperSnakePropertyConvention : IPropertyConvention
        {
            public void Apply(IPropertyInstance instance)
            {
                // 자동으로 컬럼명 적용 (Property.Name 사용)
                instance.Column(NamingUtils.ToSnakeUpperCase(instance.Property.Name));
            }
        }

        // Id 컬럼 컨벤션 (예: Id -> ID, UserId -> USER_ID)
        private class UpperSnakeIdConvention : IIdConvention
        {
            public void Apply(IIdentityInstance instance)
            {
                instance.Column(NamingUtils.ToSnakeUpperCase(instance.Name));
            }
        }

    }
}