package android.example.shopup.fragments;

import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Product;
import android.example.shopup.MyFunctions;
import android.example.shopup.Photo;
import android.example.shopup.R;
import android.os.Bundle;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ProductItemFragment extends Fragment {

    private DatabaseHelper myDb;
    private TextView nameTextView, priceTextView, categoryTextView, codeTextView, salePriceTextView;
    private ImageView photoImageView;
    private Button deleteProductButton;
    private Product product;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_item_product, container, false);

        nameTextView = rootView.findViewById(R.id.productNameTextView);
        priceTextView = rootView.findViewById(R.id.productPriceTextView);
        categoryTextView = rootView.findViewById(R.id.productCategoryTextView);
        codeTextView = rootView.findViewById(R.id.productBarCodeTextView);
        photoImageView = rootView.findViewById(R.id.productItemPhotoImageView);
        deleteProductButton = rootView.findViewById(R.id.deleteProductButton);
        salePriceTextView = rootView.findViewById(R.id.productSalePriceTextView);

        myDb = new DatabaseHelper(getContext());

        int id = -1;
        if(getArguments() != null) {
            id = getArguments().getInt("id");
        }

        product = myDb.getProduct("id_product=" + id);

        nameTextView.setText(MyFunctions.firstLetterToUpperCase(product.getName()));

        Log.i("Price", product.getPrice().toString());
        Log.i("Sale price", product.getSalePrice().toString());

        if(product.getPrice() != 0){
            priceTextView.setText(product.getPrice().toString() + " " + getContext().getResources().getString(R.string.currency));
        }else{
            priceTextView.setText(MyFunctions.firstLetterToUpperCase(getContext().getResources().getString(R.string.no_data)));
        }

        if(product.getSalePrice() != 0){
           salePriceTextView.setText(product.getSalePrice().toString() + " " + getContext().getResources().getString(R.string.currency));
        }else{
            salePriceTextView.setText(MyFunctions.firstLetterToUpperCase(getContext().getResources().getString(R.string.no_data)));
        }

        categoryTextView.setText(MyFunctions.firstLetterToUpperCase(product.getCategory()));
        codeTextView.setText(product.getBarCode().toString());
        photoImageView.setImageBitmap(Photo.rotateBitmap(Photo.getProductPhoto(getContext(), product.getBarCode().toString()), 0f));

        Photo.scaleImage(getContext(), photoImageView);

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteProduct(product.getId());
                myDb.deletePurchases(product.getId());

                deleteProductPhoto();

                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    public void deleteProductPhoto(){
        File photoFile = Photo.getPhotoFile(getContext(), product.getBarCode().toString());
        Log.i("File exists", String.valueOf(photoFile.exists()));
        if(photoFile.exists()){
            photoFile.delete();
        }

    }
}
