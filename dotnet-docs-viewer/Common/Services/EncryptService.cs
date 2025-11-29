using Common.Crypto;
using Common.Utils;

namespace Common.Services
{
    public class EncryptService : IHostedService
    {
        private IAesStringEncryptor Encryptor { get; }
        
        public EncryptService()
        {
            Encryptor = new AesStringEncryptor(password: "mo2ver", iterationCount: 1000);
        }

        public Task StartAsync(CancellationToken cancellationToken)
        {
            EncryptUtils.Initialize(Encryptor);
            return Task.CompletedTask;
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            return Task.CompletedTask;
        }
    }
}