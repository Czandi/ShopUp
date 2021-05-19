package android.example.shopup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class MyFunctions {

    public static Float priceConverter(String s){
        Float res;

        if(s.equals("")){
            res = null;
        }else{
            if(s.contains(",")){
                s = s.replace(",", ".");
            }
            String newS = String.format("%.02f", Float.parseFloat(s));
            newS = newS.replace(",", ".");
            res = Float.valueOf(newS);
        }

        return res;
    }

    //Formatting date
    public static String dateFormatter(Context context, String d){
        Date date = new Date(context, d);
        return date.getYear().toString() + "/" + date.getMonthName() + "/" + date.getDay();
    }

    public static String firstLetterToUpperCase(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void closeKeyboard(Activity activity){
        if(activity.getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            activity.getCurrentFocus().clearFocus();
        }
    }

    public static String deleteAllLastSpaces(String s){
        StringBuilder myString = null;

        if(s.length() > 0){
            Integer i = s.length()-1;
            myString = new StringBuilder(s);

            while(s.charAt(i) == ' '){
                myString.deleteCharAt(i);
                i--;
            }
        }

        if(myString == null){
            return "";
        }else{
            return myString.toString();
        }
    }

    public static List<String> firstLettersOfListItemsToUpperCase(List<String> list){
        List<String> res;

        if (list == null){
            res = null;
        }else if (!list.isEmpty()){
            res = new ArrayList<>();
            for(Integer i = 0; i < list.size(); i++){
                res.add(firstLetterToUpperCase(list.get(i)));
            }

        }else{
            res = null;
        }

        return res;
    }
}
