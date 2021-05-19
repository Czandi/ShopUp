package android.example.shopup.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.MyFunctions;
import android.example.shopup.Photo;
import android.example.shopup.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AddProductFragment extends Fragment implements View.OnClickListener{

    private TextView nameEditText;
    private AutoCompleteTextView categoryEditText;
    private EditText codeEditText;
    private DatabaseHelper myDb;
    private List<String> categoriesNames;
    private String category;
    private Boolean isCategoryNew;
    private Button addProductButton, addPhotoButton;
    private Context mContext;
    private ImageView photoImageView;
    private Bitmap photo;

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
        //Close keyboard after click on background or header
        if((v.getId() == R.id.addProductBackgroundLayout || v.getId() == R.id.addProductHeaderTextView)) {
            MyFunctions.closeKeyboard(getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_add_product, container, false);

        myDb = new DatabaseHelper(mContext);

        nameEditText = rootView.findViewById(R.id.productNameEditText);
        categoryEditText = rootView.findViewById(R.id.productCategoryEditText);
        codeEditText =  rootView.findViewById(R.id.productCodeEditText);
        addProductButton =  rootView.findViewById(R.id.addProductButton);
        addPhotoButton = rootView.findViewById(R.id.addPhotoButton);
        photoImageView = rootView.findViewById(R.id.productPhotoImageView);

        //Adding possibility to close keyboard by clicking on the background or header
        TextView headerTextView =  rootView.findViewById(R.id.addProductHeaderTextView);
        ConstraintLayout backgroundLayout =  rootView.findViewById(R.id.addProductBackgroundLayout);
        headerTextView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);

        //Adding categories names list for autocomplete edit text
        categoriesNames = myDb.getNamesList("categories");
        ArrayAdapter<String> categoriesNamesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MyFunctions.firstLettersOfListItemsToUpperCase(categoriesNames));
        categoryEditText.setAdapter(categoriesNamesAdapter);

        //Checking if scanner passed on bar code and setting it
        if(getArguments() != null){
            Long barCode = getArguments().getLong("barCode");
            if(barCode != null){
                codeEditText.setText(barCode.toString());
            }
        }

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

        return rootView;
    }

    private void addPhoto(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = mContext.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                photoImageView.setImageBitmap(Photo.rotateBitmap(selectedImage, 90f));
                photo = Photo.scaleImage(mContext, photoImageView);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    //Checking if any data are empty
    private boolean validation(){
        if(nameEditText.getText().toString().equals("") ||
                categoryEditText.getText().toString().equals("") ||
                codeEditText.getText().toString().equals("")){
            Toast.makeText(mContext, getResources().getString(R.string.warning_insert_data), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Checking if any data are new
    private Boolean checkCategory(String s){
        Boolean isNew = false;
        if(categoriesNames == null || !categoriesNames.contains(s.toLowerCase())){
            isNew = true;
        }

        return isNew;
    }

    private boolean checkIfImageExists(){
        if(photoImageView.getDrawable() != null){
            return true;
        }else{
            return false;
        }
    }

    //Inserting record to database
    private void addDataToDB(){
        if(isCategoryNew){
            myDb.insertCategory(category);
        }

        String productName = nameEditText.getText().toString();
        Long barCode = Long.parseLong(codeEditText.getText().toString());
        Integer idCategory = myDb.getCategory("name='" + category + "'").getId();

        myDb.insertProduct(productName, barCode, idCategory);
    }

    public void addProduct(){
        if(validation()){
            category = MyFunctions.deleteAllLastSpaces(categoryEditText.getText().toString().toLowerCase());
            isCategoryNew = checkCategory(category);

            if(isCategoryNew){

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(getResources().getString(R.string.dialog_new_category_title));

                String message = getResources().getString(R.string.dialog_new_category_message) + MyFunctions.firstLetterToUpperCase(category);

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
        };
    };

    private void completing(){
        addDataToDB();
        if (checkIfImageExists()) {
            Photo.saveToInternalStorage(mContext, photo, codeEditText.getText().toString());
        }
        MyFunctions.closeKeyboard(getActivity());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).addToBackStack(null).commit();
    }

    //Restoring fragment to starting view
    private void reloadFragment(){
        Toast.makeText(mContext, getResources().getString(R.string.added_product), Toast.LENGTH_SHORT);
        nameEditText.setText("");
        categoryEditText.setText("");
        codeEditText.setText("");
        photoImageView.setImageResource(0);
        if(getActivity().getCurrentFocus() != null){
            getActivity().getCurrentFocus().clearFocus();
        }
    }

}