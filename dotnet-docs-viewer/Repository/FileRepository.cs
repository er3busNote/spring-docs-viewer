using DB.Model;
using ISession = NHibernate.ISession;

namespace WebApp.Repository
{
    public class FileRepository : Repository<FileInfoBaseModel>
    {
        public FileRepository(ISession session) : base(session) {}

        public async Task<FileInfoBaseModel?> FindByIdAsync(long id) => await Session.GetAsync<FileInfoBaseModel>(id);
    }
}