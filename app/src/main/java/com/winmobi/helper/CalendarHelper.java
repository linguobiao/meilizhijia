package com.winmobi.helper;

import java.util.Calendar;

/**
 * Created by linguobiao on 16/8/19.
 */
public class CalendarHelper {

    /**
     * 加一日
     *
     * @param cal
     * @return
     */
    public static Calendar addADay(Calendar cal) {
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);

        return cal;
    }

    /**
     * 减一天
     *
     * @param cal
     * @return
     */
    public static Calendar minADay(Calendar cal) {
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);

        return cal;
    }

    /**
     * 减一年
     * @param cal
     * @return
     */
    public static Calendar minAYear(Calendar cal) {
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        return cal;
    }

}
