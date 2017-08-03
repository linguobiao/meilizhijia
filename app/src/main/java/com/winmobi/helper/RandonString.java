package com.winmobi.helper;

import java.util.Random;

/**
 * Created by luocan on 2016/7/24.
 */
public class RandonString {

    public static String getRandomStringByLength(int length) {
        String base = "klasndbnuinpiokpqnjweuihyusd";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
