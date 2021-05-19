package android.example.shopup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.fragments.AddProductFragment;
import android.example.shopup.fragments.ProductItemFragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public  class ScannerActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler{
    private ZXingScannerView scannerView;
    private List<Long> barCodesList;
    private DatabaseHelper myDb;
    private Long barCode;
    private Integer id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},5);
            }
        }

        myDb = new DatabaseHelper(this);
        barCodesList = myDb.getBarcodesList();
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        scannerView = new ZXingScannerView(this);
        contentFrame.addView(scannerView);
    }
    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        barCode = Long.parseLong(result.getText());

        if(barCodesList != null){
            if(barCodesList.contains(Long.parseLong(result.getText()))){
                id = myDb.getProduct("Bar_code=" + barCode).getId();
                startFragment("show");
//                ProductItemFragment fragment = new ProductItemFragment();
//                Bundle b = new Bundle();
//                b.putInt("id", myDb.getProduct("Bar_code=" + barCode).getId());
//                fragment.setArguments(b);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "on_back_pressed").commit();
            }else{
                startFragment("add");
            }
        }else{
            startFragment("add");
        }



//        barCode = Long.parseLong(result.getText());
//        onBackPressed();
//        if(barcodesList != null){
//            if(barcodesList.contains(Long.parseLong(result.getText()))){
//                Intent intent = new Intent(this, ProductActivity.class);
//                Bundle b = new Bundle();
//                b.putInt("id", myDb.getProduct("Bar_code=" + barCode).getId());
//                intent.putExtras(b);
//                startActivity(intent);
//            }else{
//                startAddNewProduct();
//            }
//        }else{
//            startAddNewProduct();
//        }

    }

    private void startFragment(String fragment){
        Intent intent = new Intent();
        if(fragment == "add"){
            intent.putExtra("barCode", barCode);
            setResult(1, intent);
        }else if(fragment == "show"){
            intent.putExtra("id", id);
            setResult(2, intent);
        }
        finish();
    }
}
