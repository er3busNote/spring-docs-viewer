using System.Security.Cryptography;
using WebApp;

namespace Common.Utils
{
    public static class CryptoUtil
    {
        private const int Iterations = 10000;
        private const int KeySize = 256;
        private const int IvLength = 16;

        private static string _password = null!;
        private static string _salt = null!;

        public static void Initialize(CryptoSetting setting)
        {
            _password = setting.Password;
            _salt = setting.Salt;
        }

        public static void EncryptFile(byte[] fileBytes, FileInfo targetFile)
        {
            using var aes = Aes.Create();
            aes.KeySize = KeySize;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            // Key 생성
            aes.Key = GenerateKey(_password, _salt);
            aes.GenerateIV(); // SecureRandom과 동일

            using var encryptor = aes.CreateEncryptor();
            var encryptedBytes = encryptor.TransformFinalBlock(
                fileBytes, 0, fileBytes.Length);

            // IV + 암호문 저장 (Java 코드와 동일)
            using var fs = new FileStream(targetFile.FullName, FileMode.Create);
            fs.Write(aes.IV, 0, IvLength);
            fs.Write(encryptedBytes, 0, encryptedBytes.Length);
        }

        public static byte[] DecryptFile(string filePath)
        {
            var encryptedWithIv = File.ReadAllBytes(filePath);

            using var aes = Aes.Create();
            aes.KeySize = KeySize;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            aes.Key = GenerateKey(_password, _salt);

            // IV 분리
            var iv = new byte[IvLength];
            Array.Copy(encryptedWithIv, 0, iv, 0, IvLength);
            aes.IV = iv;

            using var decryptor = aes.CreateDecryptor();
            return decryptor.TransformFinalBlock(
                encryptedWithIv,
                IvLength,
                encryptedWithIv.Length - IvLength);
        }

        private static byte[] GenerateKey(string password, string salt)
        {
            using var deriveBytes = new Rfc2898DeriveBytes(
                password,
                System.Text.Encoding.UTF8.GetBytes(salt),
                Iterations,
                HashAlgorithmName.SHA256);

            return deriveBytes.GetBytes(KeySize / 8);
        }
    }
}