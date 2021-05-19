package android.example.shopup.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.R;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Map;

public class MonthPickerDialog extends Dialog implements View.OnClickListener {

    public Button yes, no;
    public Context context;
    private String[] months;
    private DatabaseHelper myDb;
    private Activity activity;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    public MonthPickerDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.year_month_picker);
        no = findViewById(R.id.noButton);
        no.setOnClickListener(this);

        myDb = new DatabaseHelper(context);

        months = new String[] {
                context.getResources().getString(R.string.january),
                context.getResources().getString(R.string.february),
                context.getResources().getString(R.string.march),
                context.getResources().getString(R.string.april),
                context.getResources().getString(R.string.may),
                context.getResources().getString(R.string.june),
                context.getResources().getString(R.string.july),
                context.getResources().getString(R.string.august),
                context.getResources().getString(R.string.september),
                context.getResources().getString(R.string.october),
                context.getResources().getString(R.string.november),
                context.getResources().getString(R.string.december),
        };

        Calendar cal = Calendar.getInstance();

        monthPicker = findViewById(R.id.picker_month);
        yearPicker = findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(months);

        Map<String, Integer> years = getMinMaxYear();

        int year = cal.get(Calendar.YEAR);

        yearPicker.setMinValue(years.get("min"));
        yearPicker.setMaxValue(years.get("max"));
        yearPicker.setValue(year);
    }

    public Map<String, Integer> getMinMaxYear(){
        Map<String, Integer> years = new ArrayMap<>();

        years.put("min", myDb.getMinYear());
        years.put("max", myDb.getMaxYear());

        return years;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.noButton:
                TextView datePicker = activity.findViewById(R.id.purchaseMonthTextView);
                datePicker.setText(yearPicker.getValue() + " " + months[monthPicker.getValue()-1]);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
