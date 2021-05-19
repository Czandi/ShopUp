package android.example.shopup.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Store;
import android.example.shopup.MyFunctions;
import android.example.shopup.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPurchaseFragment extends Fragment implements View.OnClickListener {

    private LinearLayout purchaseScrollView;
    private Button addItemButton;
    private Button addPurchaseButton;
    private Integer idCount;
    private TextView dateTextView;
    private AutoCompleteTextView storeEditText;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatabaseHelper myDb;
    private List<String> purchasesProductsNames;
    private List<Float> purchasesPrices;
    private List<Boolean> purchasesSales;
    private List<String> productsNames;
    private List<String> storesNames;
    private String date, store, dbDate;
    private Context mContext;
    private ViewGroup rootView;

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

    @Override
    public void onClick(View v) {
        //Closing keyboard after click on background or header
        if((v.getId() == R.id.addPurchaseBackgroundLayout || v.getId() == R.id.addPurchaseHeaderTextView || v.getId() == R.id.purchaseLinearLayout) && getActivity().getCurrentFocus() != null) {
            MyFunctions.closeKeyboard(getActivity());

        //Open date picker after clicking on text view
        }else if(v.getId() == R.id.purchaseDateTextView){
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
//            Theme_AppCompat_Light_Dialog
            DatePickerDialog dialog = new DatePickerDialog(mContext, R.style.UserDialog , dateSetListener, year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_add_purchase, container, false);

        idCount = 0;
        purchaseScrollView = rootView.findViewById(R.id.purchaseLinearLayout);
        addItemButton = rootView.findViewById(R.id.addPurchaseItemButton);
        addPurchaseButton = rootView.findViewById(R.id.addPurchaseButton);
        dateTextView = rootView.findViewById(R.id.purchaseDateTextView);
        storeEditText = rootView.findViewById(R.id.purchaseStoreEditText);

        myDb = new DatabaseHelper(mContext);

        purchasesProductsNames = new ArrayList<>();
        purchasesPrices = new ArrayList<>();
        purchasesSales = new ArrayList<>();
        productsNames = myDb.getNamesList("products");

        //Adding first item to scroll view
        addPurchaseItem(idCount++);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPurchaseItem(idCount++);
            }
        });

        addPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPurchase();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            //Setting date after selecting it
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String myMonth = String.valueOf(month);
                myMonth = myMonth.length() == 1 ? "0" + myMonth : myMonth;
                String myDayOfMonth = String.valueOf(dayOfMonth);
                myDayOfMonth = myDayOfMonth.length() == 1 ? "0" + myDayOfMonth : myDayOfMonth;
                dbDate = year+myMonth+myDayOfMonth;
                String date = MyFunctions.dateFormatter(mContext, dbDate);
                dateTextView.setText(date);
            }
        };

        dateTextView.setOnClickListener(this);

        setStoresNamesAdapter();

        //Adding possibility to close keyboard by clicking on the background or header
        TextView headerTextView = rootView.findViewById(R.id.addPurchaseHeaderTextView);
        ConstraintLayout backgroundLayout = rootView.findViewById(R.id.addPurchaseBackgroundLayout);
        LinearLayout backgroundLayout2 = rootView.findViewById(R.id.purchaseLinearLayout);
        headerTextView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);
        backgroundLayout2.setOnClickListener(this);

        return rootView;
    }

    //Adding stores names list for autocomplete edit text
    private void setStoresNamesAdapter(){
        storesNames =  myDb.getNamesList("Stores");
        ArrayAdapter<String> storesNamesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MyFunctions.firstLettersOfListItemsToUpperCase(storesNames));
        storeEditText.setAdapter(storesNamesAdapter);
    }

    private Boolean getAllData(){

        //Reset all arrays
        purchasesProductsNames = new ArrayList<>();
        purchasesPrices = new ArrayList<>();
        purchasesSales = new ArrayList<>();

        ViewGroup myView = rootView.findViewById(R.id.purchaseLinearLayout);

        //Picking data from all items and validating them
        for(int i = 0; i < myView.getChildCount()-1; i++){
            ViewGroup linearLayout = (ViewGroup) ((ViewGroup) myView.getChildAt(i)).getChildAt(0);
            EditText nameEditText = (EditText) linearLayout.getChildAt(0);
            CheckBox saleCheckBox = (CheckBox) linearLayout.getChildAt(1);
            EditText priceEditText = (EditText) linearLayout.getChildAt(2);
            String name = MyFunctions.deleteAllLastSpaces(nameEditText.getText().toString());
            Float price = MyFunctions.priceConverter(MyFunctions.deleteAllLastSpaces(priceEditText.getText().toString()));
            Boolean sale = saleCheckBox.isChecked();

            if(name.equals("") || price == null){
                warningToast();
                return false;
            }else{
                if(!productsNames.contains(name.toLowerCase())){
                    Toast.makeText(mContext, getResources().getString(R.string.warning_not_all_products_are_correct), Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    purchasesProductsNames.add(name);
                    Log.i("Dodaje ", name);
                    purchasesPrices.add(price);
                    purchasesSales.add(sale);
                }
            }
        }

        store = MyFunctions.deleteAllLastSpaces(storeEditText.getText().toString());
        date = MyFunctions.deleteAllLastSpaces(dateTextView.getText().toString());

        if(date.equals(getResources().getString(R.string.pick_date)) || store.equals("")){
            warningToast();
            return false;
        }

        return true;
    }

    private void warningToast(){
        Toast.makeText(mContext, getResources().getString(R.string.warning_insert_data), Toast.LENGTH_SHORT).show();
    }

    private void addPurchaseItem(Integer index){

        ViewGroup view = (ViewGroup) View.inflate(purchaseScrollView.getContext(), R.layout.item_new_purchase, null);
        ViewGroup linearLayout = (ViewGroup) view.getChildAt(0);
        final AutoCompleteTextView name = (AutoCompleteTextView) linearLayout.getChildAt(0);
        Button button = (Button) linearLayout.getChildAt(3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) v.getParent().getParent()).removeView((View)v.getParent());
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!productsNames.contains(name.getText().toString().toLowerCase())){
                        name.setTextColor(getActivity().getColor(R.color.darkPink));
                    }
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(productsNames.contains(name.getText().toString().toLowerCase())){
                    name.setTextColor(getActivity().getColor(R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> productsNamesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MyFunctions.firstLettersOfListItemsToUpperCase(productsNames));
        name.setAdapter(productsNamesAdapter);

        view.setId(index);

        purchaseScrollView.addView(view, index);
    }

    private Integer getStoreId(String name){
        Integer storeId;
        Store store = myDb.getStore("name='" + name + "'");
        if(store == null){
            myDb.insertStore(name);
            Log.i("Info", "Tworze sklep");
            store = myDb.getStore("name='" + name + "'");
        }

        storeId = store.getId();

        return storeId;
    }

    private Boolean checkStore(String s){
        Boolean isNew = false;
        if(storesNames == null || !storesNames.contains(s.toLowerCase())){
            isNew = true;
        }

        return isNew;
    }

    private void addPurchase(){
        if(getAllData()){
            if(checkStore(store)){
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(getResources().getString(R.string.dialog_new_store_title));

                String message = getResources().getString(R.string.dialog_new_store_message) + MyFunctions.firstLetterToUpperCase(store);

                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.add), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        completing();
                    }
                });
                alertDialog.show();
            }else{
                completing();
            }
        }
    }

    private void completing(){
        addDataToDB();
        MyFunctions.closeKeyboard(getActivity());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PurchasesFragment()).commit();
    }

    private void addDataToDB(){
        for(int i = 0; i < purchasesProductsNames.size(); i++){
            Float price = purchasesPrices.get(i);
            Integer idProduct = myDb.getProduct("name='" + purchasesProductsNames.get(i).toLowerCase() + "'").getId();
            Integer idStore = getStoreId(store.toLowerCase());
            Boolean sale = purchasesSales.get(i);
            Log.i("Purchase", "Price: "+price+ " IdProduct: " + idProduct.toString() + " IdStore: " + idStore.toString() + " Sale: " + sale.toString() + " Date: " + dbDate);
            myDb.insertPurchase(price, idProduct, idStore, sale, dbDate);
            addPurchaseItem(idCount++);
        }
    }

    private void reloadFragment(){
        Toast.makeText(mContext, getResources().getString(R.string.added_product), Toast.LENGTH_SHORT);
        ViewGroup myView = rootView.findViewById(R.id.purchaseLinearLayout);
        for(int i = 0; i < idCount; i++){
            ViewGroup linearLayout = (ViewGroup) myView.getChildAt(0);
            ((ViewGroup) linearLayout.getParent()).removeView(linearLayout);

        }
        idCount = 0;
        addPurchaseItem(idCount++);
        storeEditText.setText("");
        dateTextView.setText(getResources().getString(R.string.pick_date));
        setStoresNamesAdapter();
        if(getActivity().getCurrentFocus() != null){
            getActivity().getCurrentFocus().clearFocus();
        }
    }

}
