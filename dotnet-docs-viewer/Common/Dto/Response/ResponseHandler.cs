using Newtonsoft.Json;
using WebApp.Common.Error;

namespace WebApp.Common.Dto
{
    [JsonObject]
    public abstract class ResponseHandlerBase
    {
        public virtual int Status { get; protected set; }
        public virtual string Message { get; protected set; } = null!;
    }

    public class ResponseHandler : ResponseHandlerBase
    {
        public static ResponseHandler Success(int status, string message)
        {
            return new ResponseHandler
            {
                Status = status,
                Message = message
            };
        }
        
        public static ResponseHandler Error(ErrorResponse response)
        {
            return new ResponseHandler
            {
                Status = response.Status,
                Message = response.Message
            };
        }
    }
}