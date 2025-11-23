using Newtonsoft.Json;

namespace DB.Model
{
    public interface IBaseModel
    {
        string ToJson();
    }

    /// <summary>IModel 인터페이스 구현 모델</summary>
    /// <returns>JSON 문자열</returns>
    public abstract class BaseModel : IBaseModel
    {
        public virtual String ToJson()
        {
            return JsonConvert.SerializeObject(this, Formatting.Indented);
        }
    }
}