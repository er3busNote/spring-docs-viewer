package com.docs.viewer.global.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_ISO_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_TIME_LOCAL_TIME_ZONE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 현재 날짜를 문자열로 반환
     * @return 현재 날짜
     */
    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDate.now().format(formatter);
    }

    /**
     * 현재 날짜를 문자열로 반환
     * @param pattern 날짜 포멧
     * @return 현재 날짜
     */
    public static String getCurrentDate(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.now().format(formatter);
    }

    /**
     * 현재 날짜와 시간을 문자열로 반환
     * @return 현재 날짜와 시간
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * 현재 날짜와 시간을 문자열로 반환
     * @param pattern 날짜 포멧
     * @return 현재 날짜와 시간
     */
    public static String getCurrentDateTime(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * 지정된 날짜와 시간을 UTC 형식으로 반환 (ISO 8601 표준 시간 포맷 사용)
     * @param targetDate 날짜/시간 포멧
     * @return 현재 날짜와 시간
     */
    public static String toUtcString(Date targetDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_ISO_UTC_PATTERN);
        return targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(formatter);
    }

    /**
     * 지정된 날짜와 시간을 로컬 타임존 기준으로 반환
     * @param targetDate 날짜/시간 포멧
     * @return 현재 날짜와 시간
     */
    public static String toLocalString(Date targetDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_LOCAL_TIME_ZONE_PATTERN);
        return targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(formatter);
    }

    /**
     * 두 날짜 간의 일 수 차이 계산
     * @param startDateStr 시작 날짜
     * @param endDateStr 종료 날짜
     * @param pattern 날짜 포멧
     * @return 두 날짜의 차이 값
     */
    public static long daysBetween(String startDateStr, String endDateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 날짜에 특정 일 수 추가
     * @param dateStr 지정된 날짜
     * @param days 추가할 일수
     * @param pattern 날짜 포멧
     * @return 일수 추가된 날짜
     */
    public static String addDays(String dateStr, int days, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.plusDays(days).format(formatter);
    }

    /**
     * 날짜에 특정 월 수 추가
     * @param dateStr 지정된 날짜
     * @param months 추가할 월수
     * @param pattern 날짜 포멧
     * @return 월수 추가된 날짜
     */
    public static String addMonths(String dateStr, int months, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.plusMonths(months).format(formatter);
    }

    /**
     * 특정 날짜가 오늘인지 확인
     * @param dateStr 지정된 날짜
     * @param pattern 날짜 포멧
     * @return 특정 날짜가 오늘인지 여부
     */
    public static boolean isToday(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return LocalDate.now().isEqual(date);
    }

    /**
     * 날짜가 주어진 날짜 범위 내에 있는지 확인
     * @param dateStr 지정된 날짜
     * @param startDateStr 시작 날짜
     * @param endDateStr 종료 날짜
     * @param pattern 날짜 포멧
     * @return 특정 날짜가 날짜 범위 내에 포함되어 있는지 여부
     */
    public static boolean isWithinRange(String dateStr, String startDateStr, String endDateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return (date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate));
    }

    /**
     * 특정 날짜가 주어진 날짜와 같은지 확인
     * @param dateStr1 특정 날짜 1
     * @param dateStr2 특정 날짜 2
     * @param pattern 날짜 포멧
     * @return 특정 날짜가 같은지 여부
     */
    public static boolean isSameDate(String dateStr1, String dateStr2, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date1 = LocalDate.parse(dateStr1, formatter);
        LocalDate date2 = LocalDate.parse(dateStr2, formatter);
        return date1.isEqual(date2);
    }

    public static void main(String[] args) {
        System.out.println("현재 날짜 - " + getCurrentDate());
        System.out.println("현재 날짜 - " + getCurrentDate(DATE_PATTERN));
        System.out.println("현재 날짜/시간 - " + getCurrentDateTime());
        System.out.println("현재 날짜/시간 - " + getCurrentDateTime(DATE_TIME_PATTERN));
        System.out.println("두 날짜 간의 차이 - " + daysBetween("2024-10-24", "2024-10-30", DATE_PATTERN));
        System.out.println("추가된 일수 - " + addDays("2024-10-24", 2, DATE_PATTERN));
        System.out.println("추가된 월수 - " + addMonths("2024-10-24", 2, DATE_PATTERN));
        System.out.println("오늘 날짜 여부 - " + isToday("2024-11-03", DATE_PATTERN));
        System.out.println("지정된 날짜 포함 여부 - " + isWithinRange("2024-11-03", "2024-11-02", "2024-11-04", DATE_PATTERN));
        System.out.println("두 날짜가 같은 지 여부 - " + isSameDate("2024-11-02", "2024-11-03", DATE_PATTERN));
    }
}
