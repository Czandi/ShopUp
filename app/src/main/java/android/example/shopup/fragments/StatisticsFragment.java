package android.example.shopup.fragments;

import android.content.Context;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Purchase;
import android.example.shopup.MyFunctions;
import android.example.shopup.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private DatabaseHelper myDb;
    private TextView monthly, amount, product, price, store;
    private Context mContext;
    private List<Purchase> purchases;
    private Integer averageFrequency;
    private Float averagePurchaseExpanses;
    private String mostOftenBoughtProductName;
    private Float productSumOfPrices;
    private String favoriteStore;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_statistics, container, false);

        monthly = rootView.findViewById(R.id.statisticsMonthlyTextView);
        amount = rootView.findViewById(R.id.statisticsAmountTextView);
        product = rootView.findViewById(R.id.statisticsProductTextView);
        price = rootView.findViewById(R.id.statisticsPriceTextView);
        store = rootView.findViewById(R.id.statisticsStoreTextView);

        myDb = new DatabaseHelper(mContext);

        purchases = myDb.getPurchases(null, "date");
        productSumOfPrices = 0f;

        if(purchases != null){
            calculate();

            monthly.setText(getResources().getString(R.string.monthly_frequency_1) + " " + averageFrequency + " " + getResources().getString(R.string.monthly_frequency_2));

            amount.setText(getResources().getString(R.string.average_amount) + " " + averagePurchaseExpanses + " " + getResources().getString(R.string.currency));

            Integer idProduct = myDb.getMostFrequentlyProduct();
            mostOftenBoughtProductName = myDb.getProduct("id_product =" + idProduct).getName();
            mostOftenBoughtProductName = MyFunctions.firstLetterToUpperCase(mostOftenBoughtProductName);
            product.setText(getResources().getString(R.string.most_often_bought) +" " + mostOftenBoughtProductName);

            List<Purchase> productPurchases = myDb.getPurchases("id_product=" + idProduct, null);
            for(Purchase p : productPurchases){
                productSumOfPrices += p.getPrice();
            }
            productSumOfPrices = MyFunctions.priceConverter(String.valueOf(productSumOfPrices));
            price.setText(getResources().getString(R.string.you_have_already_spent) + " " + productSumOfPrices + " " + getResources().getString(R.string.currency));

            Integer idStore = myDb.getMostFrequentlyStore();
            favoriteStore = myDb.getStore("id_store = " + idStore).getName();
            favoriteStore = MyFunctions.firstLetterToUpperCase(favoriteStore);
            store.setText(getResources().getString(R.string.your_favorite_store_is) + " " + favoriteStore);

        }else{
            monthly.setText("");
            amount.setText(getResources().getString(R.string.info_empty_database));
            product.setText("");
            price.setText("");
            store.setText("");
        }

        return rootView;
    }

    private void calculate(){
        String month = purchases.get(0).getDate().substring(4,6);
        String day = purchases.get(0).getDate().substring(6);
        List<Float> dayExpenses = new ArrayList<>();
        List<Float> allExpenses = new ArrayList<>();
        Integer frequency = 0;
        Float sumOfDay = 0f;
        List<Integer> frequencyList = new ArrayList<>();
        averagePurchaseExpanses = 0f;
        averageFrequency = 0;

        for(Purchase p : purchases){
            String s = "Id: " + p.getId() + " Id_product: " + p.getId_product() + " Id_store: " + p.getId_store() + " Price: " + p.getPrice() + " Date: " + p.getDate() + " Sale: " + p.getSale();

            String currentDay = p.getDate().substring(6);
            String currentMonth = p.getDate().substring(4,6);

            Log.i("Dzien", currentDay);
            Log.i("Miesiac", currentMonth);

            if(!currentDay.equals(day)){
                Log.i("Info", "Nowy dzien");
                sumOfDay = 0f;
                for(Float f : dayExpenses){
                    sumOfDay += f;
                }
                allExpenses.add(sumOfDay);
                frequency++;
                day = currentDay;
                dayExpenses = new ArrayList<>();
            }

            dayExpenses.add(p.getPrice());

            if(!currentMonth.equals(month)){
                Log.i("Info", "Nowy miesiac");
                frequencyList.add(frequency);
                frequency = 0;
                month = currentMonth;
            }

            Log.i("---------------------------", "---------------------------------");
        }

        for(Float f : dayExpenses){
            sumOfDay += f;
        }
        allExpenses.add(sumOfDay);
        frequencyList.add(++frequency);

        for(Float f : allExpenses){
            averagePurchaseExpanses += f;
        }
        averagePurchaseExpanses /= allExpenses.size();
        averagePurchaseExpanses = MyFunctions.priceConverter(String.valueOf(averagePurchaseExpanses));

        for(Integer i : frequencyList){
            averageFrequency += i;
        }
        double a = averageFrequency;
        double b = frequencyList.size();
        averageFrequency = (int) Math.ceil(a/b);

        Log.i("frequency list", frequencyList.toString());
        Log.i("all expenses", allExpenses.toString());
        Log.i("average purchase expanses", averagePurchaseExpanses.toString());
        Log.i("average freqency", averageFrequency.toString());
    }
}
