package android.example.shopup;

import android.content.Context;
import android.example.shopup.R;

import java.util.Calendar;

public class Date {

    private Integer year;
    private Integer month;
    private Integer day;
    private String fullDate;
    private Context context;

    public Date(Context context, String date){
        setYear(Integer.parseInt(date.substring(0, 4)));
        setMonth(Integer.parseInt(date.substring(4,6)));
        setDay(Integer.parseInt(date.substring(6)));
        setFullDate(date);
        this.context = context;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public String getDayName(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        String[] days = new String[] {
                context.getResources().getString(R.string.monday),
                context.getResources().getString(R.string.tuesday),
                context.getResources().getString(R.string.thursday),
                context.getResources().getString(R.string.wednesday),
                context.getResources().getString(R.string.friday),
                context.getResources().getString(R.string.saturday),
                context.getResources().getString(R.string.saturday),
                context.getResources().getString(R.string.sunday)
        };

        return days[calendar.get(Calendar.DAY_OF_WEEK)-1];
    }

    public String getMonthName(){
        switch(month){
            case 1:
                return context.getResources().getString(R.string.january);

            case 2:
                return context.getResources().getString(R.string.february);

            case 3:
                return context.getResources().getString(R.string.march);

            case 4:
                return context.getResources().getString(R.string.april);

            case 5:
                return context.getResources().getString(R.string.may);

            case 6:
                return context.getResources().getString(R.string.june);

            case 7:
                return context.getResources().getString(R.string.july);

            case 8:
                return context.getResources().getString(R.string.august);

            case 9:
                return context.getResources().getString(R.string.september);

            case 10:
                return context.getResources().getString(R.string.october);

            case 11:
                return context.getResources().getString(R.string.november);

            case 12:
                return context.getResources().getString(R.string.december);

            default:
                return "";
        }

    }

    public static String monthDayToNumber(Context context, String name) {
        if(name.equals(context.getResources().getString(R.string.january))) {
            return "01";
        }else if (name.equals(context.getResources().getString(R.string.february))) {
            return "02";
        }else if (name.equals(context.getResources().getString(R.string.march))) {
            return "03";
        }else if (name.equals(context.getResources().getString(R.string.april))) {
            return "04";
        }else if (name.equals(context.getResources().getString(R.string.may))) {
            return "05";
        }else if (name.equals(context.getResources().getString(R.string.july))) {
            return "06";
        }else if (name.equals(context.getResources().getString(R.string.june))) {
            return "07";
        }else if (name.equals(context.getResources().getString(R.string.august))) {
            return "08";
        }else if (name.equals(context.getResources().getString(R.string.september))) {
            return "09";
        }else if (name.equals(context.getResources().getString(R.string.october))) {
            return "10";
        }else if (name.equals(context.getResources().getString(R.string.november))) {
            return "11";
        }else if (name.equals(context.getResources().getString(R.string.december))) {
            return "12";
        }else{
            return null;
        }
    }

}
