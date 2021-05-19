package android.example.shopup.fragments;

import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Purchase;
import android.example.shopup.MyFunctions;
import android.example.shopup.R;
import android.example.shopup.adapters.PurchaseAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class PurchasesItemFragment extends Fragment {

    private DatabaseHelper myDb;
    private TextView dateTextView, priceTextView;
    private Spinner filterSpinner;
    private GridView gridView;
    private List<Purchase> purchases;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_item_purchases, container, false);

        dateTextView = rootView.findViewById(R.id.purchasesDateTextView);
        priceTextView = rootView.findViewById(R.id.purchasesPriceTextView);
        gridView = rootView.findViewById(R.id.searchGridView);

        myDb = new DatabaseHelper(getContext());

        String date = "";
        if(getArguments() != null) {
            date = getArguments().getString("date");
        }

        Log.i("Data", date);

        purchases = myDb.getPurchases("date = '" + date + "'", null);
        Float priceSum = 0f;
        for(Purchase p : purchases){
            priceSum += p.getPrice();
        }
        priceSum = MyFunctions.priceConverter(priceSum.toString());
        PurchaseAdapter adapter = new PurchaseAdapter(getContext(), purchases);
        gridView.setAdapter(adapter);

        priceTextView.setText(getResources().getString(R.string.amount) + ": " + priceSum.toString() + " " + getResources().getString(R.string.currency));
        dateTextView.setText(getResources().getString(R.string.date) + ": " + MyFunctions.dateFormatter(getContext(), date));

        return rootView;
    }
}
