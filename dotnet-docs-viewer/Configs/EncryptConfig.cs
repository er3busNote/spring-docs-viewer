using Common.Security;

namespace Configs
{
    public static class EncryptConfig
    {
        public static IServiceCollection AddEncryption(this IServiceCollection services)
        {
            services.AddSingleton<IAesStringEncryptor>(sp =>
                new AesStringEncryptor(
                    password: "mo2ver",
                    iterationCount: 1000
                )
            );

            return services;
        }
    }
}