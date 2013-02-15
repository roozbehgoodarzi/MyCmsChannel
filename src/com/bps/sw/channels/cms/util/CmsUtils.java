package com.bps.sw.channels.cms.util;

import java.util.Calendar;
import java.util.Date;

public class CmsUtils {
    public static Integer getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int createDateYear = calendar.get(Calendar.YEAR);
        int createDateMonth = calendar.get(Calendar.MONTH) + 1;
        int createDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        Integer currentDate = 0;
        String month = zeropad(String.valueOf(createDateMonth), 2);
        String day = zeropad(String.valueOf(createDateDay), 2);
        String creatDateIso = "" + createDateYear + month + day;
        currentDate = Integer.valueOf(creatDateIso);
        return currentDate;
    }

    public static String zeropad(String str, int len) {
        if (str.length() >= len)
            return str;
        for (int i = str.length(); i < len; i++)
            str = '0' + str;
        return str;
    }

    public static class PersianDate {
        private int year;
        private int month;
        private int day;
        private int[] g_days_in_month =
                {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private int[] j_days_in_month =
                {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

        public PersianDate(int gregYear, int gregMonth, int gregDay, int flag) {
            year = gregYear;
            month = gregMonth;
            day = gregDay;
        }

        public PersianDate(int gregYear, int gregMonth, int gregDay) {
            // Converts given Gregorian date to Jalali date
            convert(gregYear, gregMonth, gregDay);
        }

        public PersianDate() {
            /** Make current Jalali date */
            Date date = new Date();
            convert(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
        }

        public PersianDate(Calendar calendar) {
            /** Make current Jalali date */
            Date date = calendar.getTime();
            convert(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
        }

        private void convert(int y, int m, int d) {
            int gy, gm, gd;
            int jy, jm, jd;
            long g_day_no, j_day_no;
            int j_np;
            int i;

            gy = y - 1600;
            gm = m - 1;
            gd = d - 1;

            g_day_no = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;
            for (i = 0; i < gm; ++i)
                g_day_no += g_days_in_month[i];
            if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)))
                /* leap and after Feb */
                ++g_day_no;
            g_day_no += gd;

            j_day_no = g_day_no - 79;

            j_np = (int) j_day_no / 12053;
            j_day_no %= 12053;

            jy = (int) (979 + 33 * j_np + 4 * (j_day_no / 1461));
            j_day_no %= 1461;

            if (j_day_no >= 366) {
                jy += (j_day_no - 1) / 365;
                j_day_no = (j_day_no - 1) % 365;
            }

            for (i = 0; j_day_no >= j_days_in_month[i]; ++i) {
                j_day_no -= j_days_in_month[i];
            }
            jm = i + 1;
            jd = (int) (j_day_no + 1);

            year = jy;
            month = jm;
            day = jd;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public String toString() {
            return year + "-" + month + "-" + day;
        }

        public int Compare(PersianDate target) {
            if (year > target.year)
                return 1;
            if (year == target.year && month > target.month)
                return 1;
            if (year == target.year && month == target.month && day > target.day)
                return 1;
            if (year == target.year && month == target.month && day == target.day)
                return 0;
            return -1;
        }


        public int getDayOfYear() {
            int dayOfYear = 0;
            for (int i = 0; i < month - 1; ++i) {
                dayOfYear += j_days_in_month[i];
            }

            dayOfYear += day;

            return dayOfYear;
        }
    }


}
