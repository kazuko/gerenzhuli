package com.wdpm.personal_helper.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 2341 on 2015/11/25.
 */
public class DateAndTimeUtil {

    public static  int stringToInt(String mTimeStamp) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(mTimeStamp);
        String r = m.replaceAll("").trim();
        return Integer.parseInt(r);
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String mTimeStamp = formatter.format(curDate);
        return mTimeStamp;
    }

    public static Long getTime(String mTimeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(mTimeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time = date.getTime();
        return time;
    }


}
