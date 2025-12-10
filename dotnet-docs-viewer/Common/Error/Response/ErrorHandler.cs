using WebApp.Common.Error.Types;

namespace WebApp.Common.Error
{
    public interface IErrorHandler
    {
        ErrorResponse BuildError(ErrorCode errorCode, ErrorInfo errorMessage);
    }
    
    public class ErrorHandler : IErrorHandler
    {
        public ErrorResponse BuildError(ErrorCode errorCode, ErrorInfo errorMessage)
        {
            var meta = ErrorCodeMetadata.Get(errorCode);

            return new ErrorResponse
            {
                Code = meta.Code,
                Status = meta.Status,
                Message = meta.Message,
                Errors = errorMessage
            };
        }
    }
}