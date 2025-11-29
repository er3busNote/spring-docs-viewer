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
        private readonly ISession _session;

        public Repository(ISession session)
        {
            _session = session;
        }

        public async Task<T?> GetByIdAsync(object id)
        {
            return await _session.GetAsync<T>(id);
        }

        public async Task<IList<T>> GetAllAsync()
        {
            return await _session.Query<T>().ToListAsync();
        }

        public async Task SaveAsync(T entity)
        {
            using var tx = _session.BeginTransaction();
            await _session.SaveAsync(entity);
            await tx.CommitAsync();
        }

        public async Task UpdateAsync(T entity)
        {
            using var tx = _session.BeginTransaction();
            await _session.UpdateAsync(entity);
            await tx.CommitAsync();
        }

        public async Task DeleteAsync(T entity)
        {
            using var tx = _session.BeginTransaction();
            await _session.DeleteAsync(entity);
            await tx.CommitAsync();
        }
    }
}