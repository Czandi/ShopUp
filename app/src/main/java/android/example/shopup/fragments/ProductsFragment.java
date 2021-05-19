package android.example.shopup.fragments;

import android.content.Context;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Product;
import android.example.shopup.MyFunctions;
import android.example.shopup.adapters.ProductAdapter;
import android.example.shopup.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment {

    private Context mContext;
    private DatabaseHelper myDb;
    private AutoCompleteTextView searchEditText;
    private Button searchButton;
    private List<Product> productsList;
    private GridView gridView;
    private ProductAdapter adapter;
    private Spinner searchSpinner, filterSpinner;
    private String category, name, price, filter;
    private Map<String, List<String>> namesMap;
    private ArrayAdapter<String> autocompleteAdapter;

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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_products, container, false);

        category = getResources().getString(R.string.category);
        name = getResources().getString(R.string.name);
        price = getResources().getString(R.string.price);
        filter = "name";

        searchEditText = rootView.findViewById(R.id.searchEditText);
        searchButton = rootView.findViewById(R.id.searchButton);
        gridView = rootView.findViewById(R.id.searchGridView);
        searchSpinner = rootView.findViewById(R.id.searchSpinner);
        filterSpinner = rootView.findViewById(R.id.filterSpinner);

        myDb = new DatabaseHelper(mContext);

        productsList = myDb.getProducts(null, filter);

        if(productsList != null){
            adapter = new ProductAdapter(mContext, productsList);
            setAdapter(adapter);
        }

        //Setting search spinner items
        String[] searchSpinnerItems = new String[]{name, category};
        ArrayAdapter<String> searchSpinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, searchSpinnerItems);
        searchSpinner.setAdapter(searchSpinnerAdapter);

        //Setting filter spinner items
        String [] filterSpinnerItems = new String[]{name, price};
        ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, filterSpinnerItems);
        filterSpinner.setAdapter(filterSpinnerAdapter);

        //Adding names lists of products and categories
        namesMap = new HashMap<>();
        namesMap.put(name, MyFunctions.firstLettersOfListItemsToUpperCase(myDb.getNamesList("products")));
        namesMap.put(category, MyFunctions.firstLettersOfListItemsToUpperCase(myDb.getNamesList("categories")));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating where clause based on user's data
                String where = "";
                String search = searchEditText.getText().toString().toLowerCase();

                if(!searchEditText.getText().toString().equals("")) {
                    if (searchSpinner.getSelectedItem().toString() == name) {
                        String name = MyFunctions.deleteAllLastSpaces(search);
                        where += "name='" + name + "'";
                    } else if (searchSpinner.getSelectedItem().toString() == category) {
                        if(myDb.getCategoryId(searchEditText.getText().toString().toLowerCase()) != null){
                            String category = myDb.getCategory("name='" + search + "'").getId().toString();
                            where += "id_category=" + category;
                        }else{
                            where += "id_category=" + -1;
                        }

                    }
                }

                if(where != ""){
                    productsList = myDb.getProducts(where.toLowerCase(), filter);
                }else{
                    productsList = myDb.getProducts(null, filter);
                }

                if(productsList != null){
                    adapter = new ProductAdapter(mContext, productsList);
                    setAdapter(adapter);
                }else{
                    Toast.makeText(mContext, getResources().getString(R.string.info_found_no_data), Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Setting autocomplete list based on spinner selected item
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                autocompleteAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, namesMap.get(item));
                searchEditText.setAdapter(autocompleteAdapter);
                searchEditText.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                if(s == getResources().getString(R.string.price)){
                    filter = "price";
                }else if(s == getResources().getString(R.string.name)){
                    filter = "name";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    //Setting adapter for found products
    private void setAdapter(final ProductAdapter adapter){
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductItemFragment fragment = new ProductItemFragment();
                Bundle b = new Bundle();
                b.putInt("id", Integer.parseInt(adapter.getView(position, view, parent).getTag().toString()));
                fragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "on_back_pressed").addToBackStack(null).commit();
                Log.i("Count Fragment", String.valueOf(getActivity().getSupportFragmentManager().getBackStackEntryCount()));
            }
        });
    }
}

//TODO Filter