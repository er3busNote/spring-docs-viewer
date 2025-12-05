using System.Text;


namespace Common.Utils
{
    public static class NamingUtil
    {
        public static string ToSnakeUpperCase(string name)
        {
            if (string.IsNullOrEmpty(name)) return name;

            var sb = new StringBuilder();
            for (int i = 0; i < name.Length; i++)
            {
                char c = name[i];
                if (char.IsUpper(c) && i > 0)
                    sb.Append('_');

                sb.Append(char.ToUpper(c));
            }

            return sb.ToString();
        }
    }
}