using System.Text.Json.Serialization;

namespace WebApp.Common.Error
{
    public record ErrorResponse
    {
        public string Message { get; init; } = string.Empty;
        public string Code { get; init; } = string.Empty;
        public int Status { get; init; }
        
        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public ErrorInfo? Errors { get; init; }
    }
}