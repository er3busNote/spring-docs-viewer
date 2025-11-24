using Common.Utils;
using Newtonsoft.Json;
using FluentNHibernate.Mapping;

namespace DB.Model
{
    [JsonObject]
    public abstract class UserInfoBaseModel : BaseModel
    {
        public virtual int Id { get; set; }
        public virtual string Name { get; set; }
    }
    
    public class UserInfoBaseMap : ClassMap<UserInfoBaseModel>
    {
        public UserInfoBaseMap()
        {
            Table(NamingUtils.ToSnakeUpperCase("Users"));
            Id(x => x.Id);
            Map(x => x.Name);
            DiscriminateSubClassesOnColumn<string>(NamingUtils.ToSnakeUpperCase("UserType")); 
        }
    }
}