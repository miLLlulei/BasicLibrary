package com.mill.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static final String[] constellationArr = {"水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
            "天蝎座", "射手座", "魔羯座"};
    public static final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};

    public static long ONE_HOUR_SECONDS = 1 * 60 * 60;
    public static long ONE_DAY_SECONDS = 24 * 60 * 60;
    public static long ONE_WEEK_SECONDS = 7 * 24 * 60 * 60;

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    public static long currentTimeMillis() {// 1970 到今天零点的毫秒数
        return System.currentTimeMillis();
    }

    public static long currentTimeSecends() { // 1970 到今天零点的秒数
        return System.currentTimeMillis() / 1000;
    }

    public static long todayStartTime() // 1970 到今天零点的秒数
    {
        return GetDayStartTime(0);
    }

    public static long yesterdayStartTime() // 1970 到昨天零点的秒数
    {
        return GetDayStartTime(-1);
    }

    public static long lastWeekStartTime() // 1970 到一周前零点的秒数
    {
        return GetDayStartTime(-6);
    }

    public static long GetDayStartTime(int days) // 1970 到几天前零点的描述 几天前
    // 用负数，几天后用证书
    {
        return getDayStartTimeMillis(days).getTime() / 1000;
    }

    public static long getDateTime(int year, int month, int date, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute);

        return calendar.getTimeInMillis();
    }

    private static Date getDayStartTimeMillis(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean duringTime(int fromDay, int toDay, long time) {
        long fromTime = GetDayStartTime(fromDay);
        long toTime = GetDayStartTime(toDay);
        if (time > fromTime && time < toTime) {
            return true;
        }
        return false;
    }

    public static boolean duringToday(long lastTime) {
        return duringTime(0, 1, lastTime);
    }

    public static boolean during24Hours(long lastTime) {
        long curTime = currentTimeSecends();
        if (curTime - lastTime > ONE_DAY_SECONDS
                || lastTime - curTime > ONE_DAY_SECONDS) {
            return true;
        }
        return false;
    }

    public static String getDayString(int days) {
        return formatDate(getDayStartTimeMillis(days));
    }

    public static String getTodayString() {
        return formatDate(new Date());
    }

    public static String formatDate(Date timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(timeMillis);
        return dateString;
    }

    public static String getNowString() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getString(long lTime) {
        Date currentTime = new Date(lTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static long string2long(String src) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(src);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 两个时间，相差的年份
     */
    public static int getInvalYear(long date) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTimeInMillis(date);
        int birthYear = birthCalendar.get(Calendar.YEAR);

        return currentYear - birthYear;
    }

    public static String getBirthday(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(date));
    }

    public static long getBirthdayLong(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date data = sdf.parse(dateStr);
            return data.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据日期获取星座
     */
    public static String dateToConstellation(long date) {
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTimeInMillis(date);

        int month = birthCalendar.get(Calendar.MONTH);
        int day = birthCalendar.get(Calendar.DAY_OF_MONTH);

        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        //default to return 魔羯
        return constellationArr[11];
    }
}
