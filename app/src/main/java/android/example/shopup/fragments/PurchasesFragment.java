package android.example.shopup.fragments;

import android.content.Context;
import android.example.shopup.Database.Category;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Product;
import android.example.shopup.Database.Purchase;
import android.example.shopup.Database.Store;
import android.example.shopup.Date;
import android.example.shopup.dialogs.MonthPickerDialog;
import android.example.shopup.R;
import android.example.shopup.adapters.PurchasesAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class PurchasesFragment extends Fragment {

    private DatabaseHelper myDb;
    private Context mContext;
    private TextView dateTextView;
    private Button show;
    private List<String> dates;
    private PurchasesAdapter adapter;
    private GridView gridView;
    private String date;
    private Boolean noData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragments_puchases, container, false);

        myDb = new DatabaseHelper(mContext);

        showData();

        gridView = rootView.findViewById(R.id.purchasesGridView);
        dateTextView = rootView.findViewById(R.id.purchaseMonthTextView);
        show = rootView.findViewById(R.id.searchButton);

        List<Purchase> purchases = myDb.getPurchases(null, null);

        if(purchases != null){
            noData = false;
        }else{
            noData = true;
        }

        if(!noData){
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MonthPickerDialog dialog = new MonthPickerDialog(getContext(), getActivity());
                    dialog.show();
                }
            });

            dateTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    getDateFromDialog();
                }
            });

            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePurchases(date);
                }
            });

            updatePurchases(null);
        }else{
            dateTextView.setText(getResources().getString(R.string.no_data));
        }

        return rootView;
    }

    private void getDateFromDialog(){
        String dialogDate = dateTextView.getText().toString();
        Integer spaceIndex = dialogDate.indexOf(" ");
        if(spaceIndex != -1){
            String year = dialogDate.substring(0, spaceIndex);
            String month = dialogDate.substring(spaceIndex+1);
            date = year + Date.monthDayToNumber(getContext(), month);
        }
    }

    private void updatePurchases(String d){
        if(d != null && !d.equals("")){
            dates = myDb.getDates("date LIKE '%" + d + "%'");
        }else{
            dates = myDb.getDates(null);
        }

        if(dates != null){
            adapter = new PurchasesAdapter(mContext, dates);
            setAdapter(adapter);
        }else{
            Toast.makeText(mContext, getResources().getString(R.string.info_no_purchases_this_day), Toast.LENGTH_SHORT).show();
        }
    }

    //Setting adapter for found purchases
    private void setAdapter(final PurchasesAdapter adapter){
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PurchasesItemFragment fragment = new PurchasesItemFragment();
                Bundle b = new Bundle();
                b.putString("date", adapter.getView(position, view, parent).getTag().toString());
                fragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "on_back_pressed").addToBackStack(null).commit();
            }
        });
    }

    public void showData(){
        List<Product> productsList = myDb.getProducts(null);
        if(productsList != null){
            for(Product p : productsList){
                String s = "Id: " + p.getId() + " Name: " + p.getName() + " Bar_code: " + p.getBarCode() + " IdCategory: " + myDb.getCategory("name='" + p.getCategory() + "'").getId() + " Price: " + p.getPrice();
                Log.i("Product", s);
            }
        }else{
            Log.i("Product", "Brak rekordów");
        }

        List<Store> storesList = myDb.getStores();
        if(storesList != null){
            for(Store s : storesList){
                String p = "Id: " + s.getId() + " Name: " + s.getName();
                Log.i("Store", p);
            }
        }

        List<Category> categoryList = myDb.getCategories();
        if(categoryList != null){
            for(Category c : categoryList){
                String s = "Id: " + c.getId() + " Name: " + c.getName();
                Log.i("Category", s);
            }
        }else{
            Log.i("Category", "Brak rekordów");
        }

        List<Purchase> purchaseList = myDb.getPurchases(null, "date");
        if(purchaseList != null){
            for(Purchase p : purchaseList){
                String s = "Id: " + p.getId() + " Id_product: " + p.getId_product() + " Id_store: " + p.getId_store() + " Price: " + p.getPrice() + " Date: " + p.getDate() + " Sale: " + p.getSale();
                Log.i("Purchase", s);
            }
        }else{
            Log.i("Purchase", "Brak rekordów");
        }
    }
}