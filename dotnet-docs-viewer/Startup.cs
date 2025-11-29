using System.Text;
using Common.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json.Serialization;
using NHibernate;
using WebApp.Repository;
using ISession = NHibernate.ISession;

namespace WebApp
{
    /// <summary>기본 설정 (appsettings.json 대응)</summary>
    public class AppSettings
    {
        /// <summary>JWT 에서 사용되는 Secret</summary>
        public string Secret { get; set; } = null!;
    }
    
    /// <summary> Database 연결을 위한 설정 정보 </summary>
    public class DbSetting
    {
        /// <summary>서버 주소</summary>
        public string Server { get; set; }
        /// <summary>접속 Port</summary>
        public int Port { get; set; } = 3306;
        /// <summary>DB SID (DB 명)</summary>
        public string Database { get; set; }
        /// <summary>DB 사용자</summary>
        public string Uid { get; set; }
        /// <summary>비밀번호</summary>
        public string Pwd { get; set; }
        /// <summary>DDL 자동 생성 옵션</summary>
        public string Hbm2DdlAuto { get; set; }
        /// <summary>DB 커넥션 정보</summary>
        public string ConnectionString => $"Server={Server};Database={Database};Port={Port};Uid={Uid};Pwd={Pwd};";
    }
    
    public class Startup
    {
        private IWebHostEnvironment Env { get; init; }
        private IConfiguration Configuration { get; }
        
        public Startup(IWebHostEnvironment env, IConfiguration configuration)
        {
            Env = env;
            Configuration = configuration;
        }
        
        public void ConfigureServices(IServiceCollection services)
        {
            // ✅ Settings
            var appSetting = Configuration.GetSection("AppSettings").Get<AppSettings>() ?? throw new Exception("AppSettings 섹션이 없습니다.");
            services.AddSingleton(appSetting);
            services.Configure<DbSetting>(Configuration.GetSection("AppSettings:DB"));
            
            // ✅ NHibernate
            services.AddSingleton<DBService>();
            services.AddHostedService(sp => sp.GetRequiredService<DBService>());
            services.AddSingleton<ISessionFactory>(sp => sp.GetRequiredService<DBService>().SessionFactory);
            services.AddScoped<ISession>(sp => sp.GetRequiredService<ISessionFactory>().OpenSession());
            services.AddScoped(typeof(IRepository<>), typeof(Repository<>));
            
            // ✅ Encryption
            services.AddSingleton<EncryptService>();
            services.AddHostedService(sp => sp.GetRequiredService<EncryptService>());
            
            // ✅ REST API용 Controller만 등록
            services.AddControllers().AddNewtonsoftJson(opt => {
                opt.SerializerSettings.Converters.Add(new Newtonsoft.Json.Converters.StringEnumConverter());
                opt.SerializerSettings.NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore;
                opt.SerializerSettings.ContractResolver = new DefaultContractResolver();
                opt.SerializerSettings.ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Ignore;
            });

            // ✅ Authentication
            services.AddAuthentication(x =>
                {
                    x.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                    x.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
                })
                .AddJwtBearer(x =>
                {
                    var key = Encoding.UTF8.GetBytes(appSetting.Secret);
                    x.RequireHttpsMetadata = false;
                    x.SaveToken = true;
                    x.TokenValidationParameters = new TokenValidationParameters()
                    {
                        ValidateIssuerSigningKey = true,
                        IssuerSigningKey = new SymmetricSecurityKey(key),
                        ValidateIssuer = false,
                        ValidateAudience = false
                    };
                });
            
            // ✅ Authorization
            services.AddAuthorization();
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env, IHostApplicationLifetime lifetime, ILogger<Startup> logger)
        {
            lifetime.ApplicationStarted.Register(() => { logger.LogInformation("Web Service is Started."); });
            lifetime.ApplicationStopping.Register(() => { logger.LogInformation("Web Service is now Stopping..."); });
            lifetime.ApplicationStopped.Register(() => { logger.LogInformation("Web Service is stopped."); });
            
            app.UseHttpsRedirection();
            app.UseRouting();

            app.UseAuthentication();
            app.UseAuthorization();

            app.UseStaticFiles();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllerRoute(
                    name: "default",
                    pattern: "{controller=Home}/{action=Index}/{id?}");
            });
        }
    }
}