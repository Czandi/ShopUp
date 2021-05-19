package android.example.shopup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.example.shopup.Database.DatabaseHelper;
import android.example.shopup.fragments.AddProductFragment;
import android.example.shopup.fragments.AddPurchaseFragment;
import android.example.shopup.fragments.ProductItemFragment;
import android.example.shopup.fragments.ProductsFragment;
import android.example.shopup.fragments.PurchasesFragment;
import android.example.shopup.fragments.StatisticsFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private DatabaseHelper dbHelper;
    private DatabaseHelper myDb;
    private DrawerLayout drawer;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = getSupportFragmentManager();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        dbHelper = new DatabaseHelper(this);
        myDb = new DatabaseHelper(this);

//        myDb.clearDatabase();

//        myDb.insertProduct("Woreczki na lód", 5906900002050L, 1);
//        myDb.insertProduct("Chocapici", 987654321, 2);
//        myDb.insertProduct("Golonka", 1251225, 2);
//        myDb.insertProduct("Pasztet", 124512412, 2);
//        myDb.insertProduct("Papier toaletowy", 124124, 1);
//        myDb.insertProduct("Pomidor", 1234124, 1);
//        myDb.insertProduct("Talerz", 1241112, 2);
//        myDb.insertProduct("Woda", 12341421, 2);
//        myDb.insertProduct("Pad", 123456789, 1);
//        myDb.insertProduct("Okulary", 123456789, 1);
//        myDb.insertProduct("Karty", 123456789, 1);
//        myDb.insertProduct("Głośniki", 123456789, 1);
//        myDb.insertProduct("Biurko", 123456789, 1);
//
//        myDb.insertStore("Biedronka");
//        myDb.insertStore("Lidl");
//        myDb.insertCategory("Warzywa");
//        myDb.insertCategory("Platki");
//        myDb.insertPurchase(2.4f, 1, 1, false, "awdawd");
//        myDb.insertPurchase(7.79f, 2, 2, true, "gerge");
//        myDb.insertPurchase(9.99f, 2, 2, true, "gerge");
//        myDb.insertPurchase(8.79f, 2, 2, true, "gerge");

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()){
            case R.id.nav_scanner:
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.nav_products:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_purchases:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PurchasesFragment()).addToBackStack(null).commit();
                 break;

            case R.id.nav_add_product:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddProductFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_add_purchase:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPurchaseFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatisticsFragment()).addToBackStack(null).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.i("ResultCode", String.valueOf(resultCode));
            if(resultCode == 1) {
                Long barCode = data.getLongExtra("barCode", -1);
                Bundle bundle = new Bundle();
                bundle.putLong("barCode", barCode);
                AddProductFragment apf = new AddProductFragment();
                apf.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, apf).addToBackStack(null).commit();
            }else if(resultCode == 2){
                Integer id = data.getIntExtra("id", -1);
                ProductItemFragment fragment = new ProductItemFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        }
    }
}
