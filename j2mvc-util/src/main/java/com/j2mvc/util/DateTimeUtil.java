package com.j2mvc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by worda on 2016/8/21.
 */
public class DateTimeUtil {
    public static final String TODAY = "今天";
    public static final String YESTERDAY = "昨天";
    public static final String TOMORROW = "明天";
    public static final String BEFORE_YESTERDAY = "前天";
    public static final String AFTER_TOMORROW = "后天";
    public static final String SUNDAY = "周日";
    public static final String MONDAY = "周一";
    public static final String TUESDAY = "周二";
    public static final String WEDNESDAY = "周三";
    public static final String THURSDAY = "周四";
    public static final String FRIDAY = "周五";
    public static final String SATURDAY = "周六";

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 将日期信息转换成今天、明天、后天、星期
     * @param date
     * @return
     */
    public static String getDateDetail(String date){
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        try {
            today.setTime(new Date());
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);

            target.setTime(dateTimeFormat.parse(date));
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
        int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        return showDateDetail(xcts,target);

    }
    /**
     * 将日期信息转换成星期
     * @param date
     * @return
     */
    public static String getWeekDay(Date date){
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        target.set(Calendar.HOUR, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        return getWeekDay(target);

    }
    /**
     * 将日期信息转换成星期
     * @param source
     * @return
     */
    public static int getDayOfWeek(String source){
        Date date = null;
        try {
            date = dateFormat.parse(source);
            Calendar target = Calendar.getInstance();
            target.setTime(date);
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
            return target.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }
    /**
     * 将日期差显示为日期或者星期
     * @param xcts
     * @param target
     * @return
     */
    private static String showDateDetail(int xcts, Calendar target){
        switch(xcts){
            case 0:
                return TODAY;
            case 1:
                return TOMORROW;
            case 2:
                return AFTER_TOMORROW;
            case -1:
                return YESTERDAY;
            case -2:
                return BEFORE_YESTERDAY;
            default:
                return getWeekDay(target);

        }
    }
    /**
     * 将日期差显示为星期
     * @param target
     * @return
     */
    private static String getWeekDay(Calendar target){
        int dayForWeek = 0;
        dayForWeek = target.get(Calendar.DAY_OF_WEEK);
        switch(dayForWeek){
            case 1: return SUNDAY;
            case 2: return MONDAY;
            case 3: return TUESDAY;
            case 4: return WEDNESDAY;
            case 5: return THURSDAY;
            case 6: return FRIDAY;
            case 7: return SATURDAY;
            default:return null;
        }
    }
    /**
     * 获取两个日期之间的间隔时间文本
     * @return
     */
    public static String getDateTimeCountStr(Date startDate, Date endDate) {
        long l = getDateTimeCount(startDate,endDate);
        int day = (int)(l / (1000 * 60 * 60 * 24));
        return day+"天";
    }
    /**
     * 获取两个日期之间的间隔时间
     * @return
     */
    public static long getDateTimeCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return toCalendar.getTime().getTime() - fromCalendar.getTime().getTime();
    }

    /**
     * A大于B
     * @param a
     * @param b
     * @return
     */
    public static boolean compareDate(String a,String b){
        if(StringUtils.isEmpty(a) ||StringUtils.isEmpty(b))
            return false;
        try {
            Date ad = dateFormat.parse(a);
            Date bd = dateFormat.parse(b);
            return ad.getTime() > bd.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 相同
     * @param a
     * @param b
     * @return
     */
    public static boolean isSame(String a,String b){
        if(StringUtils.isEmpty(a) ||StringUtils.isEmpty(b))
            return false;
        try {
            Date ad = dateFormat.parse(a);
            Date bd = dateFormat.parse(b);
            return ad.getTime() == bd.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 计算指定月份最大天数
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int calMonthDays(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day); //2007/2/1
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return maxDay;
    }

    /**
     * 输出自定义日期
     * @param str
     * @return
     */
    public static  String parseDateStr(String str){
        if(StringUtils.isEmpty(str))
            return "" ;
        Calendar nc = Calendar.getInstance();
        nc.setTime(new Date());

        Calendar tc = Calendar.getInstance();
        try {
            tc.setTime(DateTimeUtil.dateFormat.parse(str));
            if(tc.get(Calendar.YEAR) == nc.get(Calendar.YEAR)){
                long nowTime = nc.getTime().getTime();
                long targetTime = tc.getTime().getTime();
                long oneDayTime = 1000*60*60*24;
                if(targetTime > nowTime){
                    // 计算明天和后天
                    long l = targetTime - nowTime;
                    float o = (float)l / (float)oneDayTime;
                    if(o<1){
                        return "今天";
                    }else if(o >1 && o<2){
                        return "明天";
                    }else if(o >2 && o<3){
                        return "后天";
                    }else{
                        return (tc.get(Calendar.MONTH)+1)+"月"+tc.get(Calendar.DATE)+"日";
                    }
                }else{
                    // 计算昨天和前天
                    long l = nowTime - targetTime ;
                    float o = (float)l / (float)oneDayTime;
                    if(o<1){
                        return "今天";
                    }else if(o >1 && o<2){
                        return "昨天";
                    }else if(o >2 && o<3){
                        return "前天";
                    }else{
                        return (tc.get(Calendar.MONTH)+1)+"月"+tc.get(Calendar.DATE)+"日";
                    }
                }
            }else{
                return tc.get(Calendar.YEAR)+"年"+(tc.get(Calendar.MONTH)+1)+"月"+tc.get(Calendar.DATE)+"日";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
