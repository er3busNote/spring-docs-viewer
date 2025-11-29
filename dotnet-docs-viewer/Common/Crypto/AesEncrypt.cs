using System.Security.Cryptography;

namespace Common.Crypto
{
    public interface IAesStringEncryptor
    {
        string Encrypt(string plainText);
        string Decrypt(string cipherText);
    }
    
    public class AesStringEncryptor : IAesStringEncryptor
    {
        private readonly string _password;
        private readonly int _iterationCount;

        public AesStringEncryptor(string password, int iterationCount)
        {
            _password = password;
            _iterationCount = iterationCount;
        }

        public string Encrypt(string plainText)
        {
            using var aes = Aes.Create();
            using var key = new Rfc2898DeriveBytes(
                _password, 16, _iterationCount, HashAlgorithmName.SHA512);

            aes.Key = key.GetBytes(32);
            aes.GenerateIV();

            using var encryptor = aes.CreateEncryptor();
            using var ms = new MemoryStream();
            ms.Write(aes.IV, 0, aes.IV.Length);

            using (var cs = new CryptoStream(ms, encryptor, CryptoStreamMode.Write))
            using (var sw = new StreamWriter(cs))
            {
                sw.Write(plainText);
            }

            return Convert.ToBase64String(ms.ToArray());
        }

        public string Decrypt(string cipherText)
        {
            var fullCipher = Convert.FromBase64String(cipherText);

            using var aes = Aes.Create();
            using var key = new Rfc2898DeriveBytes(
                _password, 16, _iterationCount, HashAlgorithmName.SHA512);

            aes.Key = key.GetBytes(32);

            var iv = new byte[16];
            Array.Copy(fullCipher, iv, iv.Length);
            aes.IV = iv;

            using var decryptor = aes.CreateDecryptor();
            using var ms = new MemoryStream(fullCipher, iv.Length, fullCipher.Length - iv.Length);
            using var cs = new CryptoStream(ms, decryptor, CryptoStreamMode.Read);
            using var sr = new StreamReader(cs);

            return sr.ReadToEnd();
        }
    }
}