using System.Globalization;

namespace Common.Utils
{
    public static class DateUtil
    {
        private const string DatePattern = "yyyy-MM-dd";
        private const string DateFormat = "yyyyMMdd";
        private const string TimeFormat = "HH:mm";
        private const string DateTimePattern = "yyyy-MM-dd HH:mm";
        private const string DateTimeIsoUtcPattern = "yyyy-MM-dd'T'HH:mm:ss.fff'Z'";
        private const string DateTimeLocalTimeZonePattern = "yyyy-MM-dd'T'HH:mm:ss";
        private const string DateTimeFormat = "yyyyMMddHHmmss";
        private const string TimestampFormat = "yyyyMMddHHmmssfff";

        /* ===================== 현재 날짜 ===================== */

        /// <summary>현재 날짜를 문자열로 반환</summary>
        /// <returns>현재 날짜</returns>
        public static string GetCurrentDate()
        {
            return DateTime.Now.ToString(DateFormat);
        }

        /// <summary>현재 날짜를 문자열로 반환</summary>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>현재 날짜</returns>
        public static string GetCurrentDate(string pattern)
        {
            return DateTime.Now.ToString(pattern);
        }

        /* ===================== 현재 날짜 / 시간 ===================== */

        /// <summary>현재 날짜와 시간을 문자열로 반환</summary>
        /// <returns>현재 날짜와 시간</returns>
        public static string GetCurrentDateTime()
        {
            return DateTime.Now.ToString(DateTimeFormat);
        }

        /// <summary>현재 날짜와 시간을 문자열로 반환</summary>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>현재 날짜와 시간</returns>
        public static string GetCurrentDateTime(string pattern)
        {
            return DateTime.Now.ToString(pattern);
        }

        /* ===================== UTC / Local 변환 ===================== */

        /// <summary>지정된 날짜와 시간을 UTC 형식으로 반환 (ISO 8601 표준 시간 포맷 사용)</summary>
        /// <param name="targetDate">날짜/시간 포멧</param>
        /// <returns>현재 날짜와 시간</returns>
        public static string ToUtcString(DateTime targetDate)
        {
            return targetDate
                .ToUniversalTime()
                .ToString(DateTimeIsoUtcPattern, CultureInfo.InvariantCulture);
        }

        /// <summary>지정된 날짜와 시간을 로컬 타임존 기준으로 반환</summary>
        /// <param name="targetDate">날짜/시간 포멧</param>
        /// <returns>현재 날짜와 시간</returns>
        public static string ToLocalString(DateTime targetDate)
        {
            return targetDate
                .ToLocalTime()
                .ToString(DateTimeLocalTimeZonePattern);
        }

        /* ===================== 날짜 계산 ===================== */

        /// <summary>두 날짜 간의 일 수 차이 계산</summary>
        /// <param name="startDateStr">시작 날짜</param>
        /// <param name="endDateStr">종료 날짜</param>
        /// <returns>두 날짜의 차이 값</returns>
        public static long DaysBetween(string startDateStr, string endDateStr, string pattern)
        {
            var start = DateTime.ParseExact(startDateStr, pattern, CultureInfo.InvariantCulture);
            var end = DateTime.ParseExact(endDateStr, pattern, CultureInfo.InvariantCulture);
            return (end.Date - start.Date).Days;
        }

        /// <summary>날짜에 특정 일 수 추가</summary>
        /// <param name="dateStr">지정된 날짜</param>
        /// <param name="days">추가할 일수</param>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>일수 추가된 날짜</returns>
        public static string AddDays(string dateStr, int days, string pattern)
        {
            var date = DateTime.ParseExact(dateStr, pattern, CultureInfo.InvariantCulture);
            return date.AddDays(days).ToString(pattern);
        }

        /// <summary>날짜에 특정 월 수 추가</summary>
        /// <param name="dateStr">지정된 날짜</param>
        /// <param name="months">추가할 월수</param>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>월수 추가된 날짜</returns>
        public static string AddMonths(string dateStr, int months, string pattern)
        {
            var date = DateTime.ParseExact(dateStr, pattern, CultureInfo.InvariantCulture);
            return date.AddMonths(months).ToString(pattern);
        }

        /* ===================== 날짜 비교 ===================== */

        /// <summary>특정 날짜가 오늘인지 확인</summary>
        /// <param name="dateStr">지정된 날짜</param>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>특정 날짜가 오늘인지 여부</returns>
        public static bool IsToday(string dateStr, string pattern)
        {
            var date = DateTime.ParseExact(dateStr, pattern, CultureInfo.InvariantCulture);
            return date.Date == DateTime.Today;
        }

        /// <summary>날짜가 주어진 날짜 범위 내에 있는지 확인</summary>
        /// <param name="dateStr">지정된 날짜</param>
        /// <param name="startDateStr">시작 날짜</param>
        /// <param name="endDateStr">종료 날짜</param>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>특정 날짜가 날짜 범위 내에 포함되어 있는지 여부</returns>
        public static bool IsWithinRange(string dateStr, string startDateStr, string endDateStr, string pattern)
        {
            var date = DateTime.ParseExact(dateStr, pattern, CultureInfo.InvariantCulture);
            var start = DateTime.ParseExact(startDateStr, pattern, CultureInfo.InvariantCulture);
            var end = DateTime.ParseExact(endDateStr, pattern, CultureInfo.InvariantCulture);

            return date.Date >= start.Date && date.Date <= end.Date;
        }

        /// <summary>특정 날짜가 주어진 날짜와 같은지 확인</summary>
        /// <param name="dateStr1">특정 날짜 1</param>
        /// <param name="dateStr2">특정 날짜 2</param>
        /// <param name="pattern">날짜 포멧</param>
        /// <returns>특정 날짜가 같은지 여부</returns>
        public static bool IsSameDate(string dateStr1, string dateStr2, string pattern)
        {
            var date1 = DateTime.ParseExact(dateStr1, pattern, CultureInfo.InvariantCulture);
            var date2 = DateTime.ParseExact(dateStr2, pattern, CultureInfo.InvariantCulture);

            return date1.Date == date2.Date;
        }
        
        public static void Run()
        {
            Console.WriteLine("현재 날짜 - " + GetCurrentDate());
            Console.WriteLine("현재 날짜 - " + GetCurrentDate(DatePattern));
            Console.WriteLine("현재 날짜/시간 - " + GetCurrentDateTime());
            Console.WriteLine("현재 날짜/시간 - " + GetCurrentDateTime(DateTimePattern));
            Console.WriteLine("두 날짜 차이 - " + DaysBetween("2024-10-24", "2024-10-30", DatePattern));
            Console.WriteLine("추가된 날짜 - " + AddDays("2024-10-24", 2, DatePattern));
        }
    }
}