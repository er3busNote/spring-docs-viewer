using Common.Crypto;

namespace Common.Utils
{
    public static class EncryptUtil
    {
        private static IAesStringEncryptor _encryptor;

        /// <summary> DI 초기화 </summary>
        public static void Initialize(IAesStringEncryptor encryptor)
        {
            _encryptor = encryptor;
        }

        private static string Encrypt(string input)
        {
            return _encryptor.Encrypt(input);
        }

        private static string Decrypt(string input)
        {
            return _encryptor.Decrypt(input.Replace(" ", "+"));
        }

        public static string GetEncryptor(int? value)
        {
            return value.HasValue
                ? Encrypt(value.Value.ToString())
                : string.Empty;
        }

        public static int? GetDecryptor(string value)
        {
            if (string.IsNullOrEmpty(value))
                return null;

            return int.Parse(Decrypt(value));
        }
    }
}