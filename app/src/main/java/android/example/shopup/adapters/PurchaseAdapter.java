package android.example.shopup.adapters;

import android.content.Context;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.Database.Purchase;
import android.example.shopup.MyFunctions;
import android.example.shopup.Photo;
import android.example.shopup.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PurchaseAdapter extends ArrayAdapter<Purchase> {

    public PurchaseAdapter(@NonNull Context context, List<Purchase> purchases) {
        super(context, 0, purchases);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Purchase purchase = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
            convertView.setTag(purchase.getId());
        }

        TextView name = convertView.findViewById(R.id.itemNameTextView);
        TextView price = convertView.findViewById(R.id.itemPriceTextView);
        ImageView photo = convertView.findViewById(R.id.itemPhotoImageView);

        DatabaseHelper myDb = new DatabaseHelper(getContext());

        name.setText(MyFunctions.firstLetterToUpperCase(myDb.getProduct("id_product = " + purchase.getId_product()).getName()));
        price.setText(purchase.getPrice() + " " + getContext().getResources().getString(R.string.currency));
        photo.setImageBitmap(Photo.rotateBitmap(Photo.getProductPhoto(getContext(), myDb.getProduct("id_product = " + purchase.getId_product()).getBarCode().toString()), 0f));
        Photo.scaleImage(getContext(), photo);

        return convertView;
    }
}
