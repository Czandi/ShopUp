package android.example.shopup.adapters;

import android.content.Context;
import android.example.shopup.Date;
import android.example.shopup.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PurchasesAdapter extends ArrayAdapter<String> {

    private Context context;

    public PurchasesAdapter(Context context, List<String> dates){
        super(context, 0, dates);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String d = getItem(position);
        Date date = new Date(context, d);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_purchase, parent, false);
            convertView.setTag(date.getFullDate());
        }

        TextView dayNameTextView = convertView.findViewById(R.id.itemDayNameTextView);
        TextView monthNameTextView = convertView.findViewById(R.id.itemMonthNameTextView);

        dayNameTextView.setText(date.getDayName());
        monthNameTextView.setText(date.getDay().toString() + " " + date.getMonthName());

        return convertView;
    }
}
