using Newtonsoft.Json;

namespace WebApp.Dto
{
    public interface IBaseDto
    {
        string ToJson();
    }

    /// <summary>IDto 인터페이스 구현 모델</summary>
    /// <returns>JSON 문자열</returns>
    public abstract class BaseDto : IBaseDto
    {
        public virtual String ToJson() => JsonConvert.SerializeObject(this, Formatting.Indented);
    }
}