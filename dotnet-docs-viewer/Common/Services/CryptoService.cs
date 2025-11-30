using Common.Utils;
using Microsoft.Extensions.Options;
using WebApp;

namespace Common.Services
{
    public class CryptoService : IHostedService
    {
        private readonly IOptions<CryptoSetting> _settings;
        
        public CryptoService(IOptions<CryptoSetting> settings)
        {
            _settings = settings;
        }

        public Task StartAsync(CancellationToken cancellationToken)
        {
            CryptoUtil.Initialize(_settings.Value);
            return Task.CompletedTask;
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            return Task.CompletedTask;
        }
    }
}