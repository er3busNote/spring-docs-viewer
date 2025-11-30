namespace Common.Utils
{
    public static class FileUtil
    {
        private static readonly string PROJECT_DIRECTORY = Directory.GetCurrentDirectory();

        /// <summary>업로드 디렉토리 경로 생성 (날짜 기준)</summary>
        public static string GetDirectory(string filepath, string targetFolder)
        {
            var directoryPath = Path.Combine(
                targetFolder,
                DateUtil.GetCurrentDate()
            );

            return Path.Combine(filepath, directoryPath);
        }

        /// <summary>업로드 디렉토리 생성 (없으면 생성)</summary>
        public static string GetUploadDirectory(string uploadDirectory)
        {
            CreateDirectory(uploadDirectory);
            return uploadDirectory;
        }

        /// <summary>실제 파일 객체 반환</summary>
        public static FileInfo GetTargetFile(string targetFilePath)
        {
            return new FileInfo(
                Path.Combine(PROJECT_DIRECTORY, targetFilePath)
            );
        }

        /// <summary>UUID 기반 파일 경로 반환</summary>
        public static string GetFilePath(string uploadDirectory)
        {
            return Path.Combine(
                uploadDirectory,
                Guid.NewGuid().ToString()
            );
        }

        /// <summary>파일 확장자 반환</summary>
        public static string GetFileExtension(string fileName)
        {
            return Path.GetExtension(fileName)
                .TrimStart('.');
        }

        /// <summary>확장자 제거</summary>
        public static string RemoveFileExtension(string fileName)
        {
            return Path.GetFileNameWithoutExtension(fileName);
        }

        /// <summary>디렉토리 생성</summary>
        private static void CreateDirectory(string uploadDirectory)
        {
            if (!Directory.Exists(uploadDirectory))
            {
                Directory.CreateDirectory(uploadDirectory);
            }
        }
    }
}