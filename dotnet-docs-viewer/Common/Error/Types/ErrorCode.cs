namespace WebApp.Common.Error.Types
{
    public enum ErrorCode
    {
        FILETYPE_MAPPING_INVALID,
        INTERNAL_SERVER_ERROR
    }
    
    public static class ErrorCodeMetadata
    {
        private static readonly Dictionary<ErrorCode, (string Code, string Message, int Status)> _map
            = new()
            {
                {
                    ErrorCode.FILETYPE_MAPPING_INVALID,
                    ("CM_007", "지원되지 않는 파일타입입니다.", 400)
                },
                {
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    ("CM_100", "서버 에러.", 500)
                }
            };

        public static (string Code, string Message, int Status) Get(ErrorCode errorCode)
        {
            return _map[errorCode];
        }
    }
}