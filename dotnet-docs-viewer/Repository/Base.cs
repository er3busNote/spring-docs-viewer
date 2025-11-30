using NHibernate.Linq;
using ISession = NHibernate.ISession;

namespace WebApp.Repository
{
    public interface IRepository<T> where T : class
    {
        Task<T?> GetByIdAsync(object id);
        Task<IList<T>> GetAllAsync();
        Task SaveAsync(T entity);
        Task UpdateAsync(T entity);
        Task DeleteAsync(T entity);
    }
    
    public class Repository<T> : IRepository<T> where T : class
    {
        protected readonly ISession Session;

        public Repository(ISession session)
        {
            Session = session;
        }

        public async Task<T?> GetByIdAsync(object id) => await Session.GetAsync<T>(id);

        public async Task<IList<T>> GetAllAsync() => await Session.Query<T>().ToListAsync();

        public async Task SaveAsync(T entity) => await Session.SaveAsync(entity);

        public async Task UpdateAsync(T entity) => await Session.UpdateAsync(entity);

        public async Task DeleteAsync(T entity) => await Session.DeleteAsync(entity);
    }
}