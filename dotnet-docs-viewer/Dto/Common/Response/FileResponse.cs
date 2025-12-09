using Newtonsoft.Json;

namespace WebApp.Dto
{
    [JsonObject]
    public abstract class FileResponseBase : BaseDto
    {
        public virtual byte[] Resource { get; protected set; } = null!;
        public virtual string MimeType { get; protected set; } = null!;
    }

    public class FileResponse : FileResponseBase
    {
        public static FileResponse Of(byte[] resource, string mimeType)
        {
            return new FileResponse
            {
                Resource = resource,
                MimeType = mimeType
            };
        }
    }
}