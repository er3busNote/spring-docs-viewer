using Microsoft.AspNetCore.Server.Kestrel.Core;
using Microsoft.Extensions.Logging.Console;

namespace WebApp
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            await CreateHostBuilder(args).Build().RunAsync();
        }

        private static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureServices((context, services) =>
                {
                    services.Configure<KestrelServerOptions>(context.Configuration.GetSection("Kestrel"));
                })
                .ConfigureLogging((context, config) => {
                    config
                        .SetMinimumLevel(LogLevel.Trace)
                        .AddSimpleConsole(x => { x.ColorBehavior = LoggerColorBehavior.Disabled; x.SingleLine = true; x.UseUtcTimestamp = true; x.TimestampFormat = "[MM\\/dd HH:mm:ss] "; });
                })
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>()
                        .ConfigureKestrel(options => {
                            options.Limits.MaxRequestBodySize = 10 * 1024 * 1024; // 10MB
                        });
                });
    }
}