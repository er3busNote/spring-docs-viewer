using DB.Model;
using WebApp.Repository;

namespace WebApp.Service
{
    public class UserService
    {
        private readonly IRepository<UserInfoBaseModel> _userRepository;

        public UserService(IRepository<UserInfoBaseModel> userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task CreateUserAsync(UserInfoBaseModel user)
        {
            await _userRepository.SaveAsync(user);
        }

        public async Task<UserInfoBaseModel?> GetUserAsync(long id)
        {
            return await _userRepository.GetByIdAsync(id);
        }
    }
}