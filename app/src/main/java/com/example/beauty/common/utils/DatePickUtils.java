package com.example.beauty.common.utils;

import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huanlin-zhl
 * @date 2020/5/3 10:11
 */
public class DatePickUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static Date getDate(DatePicker datePicker) {
        try {
            String yearStr = formatNum(datePicker.getYear());
            String monStr = formatNum(datePicker.getMonth() + 1);
            String dayStr = formatNum(datePicker.getDayOfMonth());
            String dateStr = yearStr + "-" + monStr + "-" + dayStr + "-08-00-00";
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initDatePicker(DatePicker datePicker, Date date){
        String formatDate = dateFormat.format(date);
        String[] dateArray = formatDate.split("-");
        datePicker.init(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, Integer.parseInt(dateArray[2]), null);
    }

    private static String formatNum(Integer num){
        if(num < 10){
            return "0" + num;
        }else{
            return "" + num;
        }
    }

}
