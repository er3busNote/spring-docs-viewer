using Common.Helper;
using Microsoft.Extensions.Options;
using NHibernate;
using WebApp;

namespace Common.Services
{
    public class DBService : IHostedService, IDisposable
    {
        private readonly IOptions<DbSetting> _settings;

        public ISessionFactory SessionFactory { get; private set; } = null!;

        public DBService(IOptions<DbSetting> settings)
        {
            _settings = settings;
        }

        public Task StartAsync(CancellationToken cancellationToken)
        {
            // NHibernate Configuration 생성
            var config = DBHelper.ConfigureNHibernate(_settings.Value);
            SessionFactory = config.BuildSessionFactory();
            return Task.CompletedTask;
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            return Task.CompletedTask;
        }

        public void Dispose()
        {
            SessionFactory?.Dispose();
        }
    }
}