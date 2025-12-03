using DB.Model;
using ISession = NHibernate.ISession;

namespace WebApp.Repository
{
    public class PreviewRepository : Repository<PreviewInfoBaseModel>
    {
        public PreviewRepository(ISession session) : base(session) {}
        
        public async Task<PreviewInfoBaseModel?> FindByIdAsync(long id) => await Session.GetAsync<PreviewInfoBaseModel>(id);
    }
}