package android.example.shopup.adapters;

import android.content.Context;
import android.example.shopup.Database.Product;
import android.example.shopup.MyFunctions;
import android.example.shopup.Photo;
import android.example.shopup.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, List<Product> products){
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
            convertView.setTag(product.getId());
        }

        TextView name = convertView.findViewById(R.id.itemNameTextView);
        TextView price = convertView.findViewById(R.id.itemPriceTextView);
        ImageView photo = convertView.findViewById(R.id.itemPhotoImageView);

        name.setText(MyFunctions.firstLetterToUpperCase(product.getName()));
        if(product.getPrice() != 0){
            price.setText(MyFunctions.firstLetterToUpperCase(getContext().getResources().getString(R.string.price)) + ": " + product.getPrice().toString() + " " + getContext().getResources().getString(R.string.currency));
        }else{
            price.setText(MyFunctions.firstLetterToUpperCase(getContext().getResources().getString(R.string.price)) + ": " +getContext().getResources().getString(R.string.no_data));
        }

        photo.setImageBitmap(Photo.rotateBitmap(Photo.getProductPhoto(getContext(), product.getBarCode().toString()), 0f));
        Photo.scaleImage(getContext(), photo);

        return convertView;
    }
}