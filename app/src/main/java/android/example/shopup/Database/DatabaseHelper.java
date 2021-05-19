package android.example.shopup.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.shopup.Date;
import android.example.shopup.MyFunctions;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME="ShopUp";
    private SQLiteDatabase myDb;
    private Context context;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        myDb = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Products (Id_product INTEGER PRIMARY KEY, Name VARCHAR, Bar_code INTEGER, Id_category INTEGER, Price FLOAT, Sale_price Float)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Stores (Id_store INTEGER PRIMARY KEY, Name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Categories (Id_category INTEGER PRIMARY KEY, Name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Purchases (Id_purchase INTEGER PRIMARY KEY, Price Float, Id_product INTEGER, Id_store INTEGER, Sale Integer, Date VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertProduct(String name, Long barCode, Integer idCategory){
        myDb.execSQL("INSERT INTO Products (Name, Bar_code, Id_category, Price, Sale_price) VALUES ('" + MyFunctions.deleteAllLastSpaces(name.toLowerCase()) + "', " + barCode + ", " + idCategory + ", 0 " + ",0)");
    }

    public void deleteProduct(Integer idProduct){
        myDb.delete("products", "id_product = " + idProduct, null);
    }

    public void deletePurchases(Integer idProduct){
        myDb.delete("purchases", "id_product = " + idProduct, null);
    }

    public void insertStore(String name) {
       if(!checkIfStoreExists(name)){
           myDb.execSQL("INSERT INTO Stores (Name) VALUES ('" + MyFunctions.deleteAllLastSpaces(name.toLowerCase()) + "')");
       }
    }

    public void insertCategory(String name){
        myDb.execSQL("INSERT INTO Categories (Name) VALUES ('" + MyFunctions.deleteAllLastSpaces(name.toLowerCase()) + "')");
    }

    public void insertPurchase(Float price, Integer idProduct, Integer idStore, Boolean sale, String date){
        myDb.execSQL("INSERT INTO Purchases (Price, Id_product, Id_store, Sale, Date) VALUES (" + price + ", " + idProduct + ", " + idStore + ", " + (sale ? 1 : -1) + ", '" + date + "')");
        updateNormalPrice(idProduct);
        updateSalePrice(idProduct);
    }

    public List<Product> getProducts(String where) {
        return getProducts(where, null);
    }

    public List<Product> getProducts(String where, String orderBy){
        List<Product> res = new ArrayList<>();

        String query = "SELECT * FROM products";

        if(where != null && !where.equals("")){
            query += " WHERE " + where;
        }

        if(orderBy != null && !orderBy.equals("")){
            query += " ORDER BY " + orderBy;
        }

        Cursor c = myDb.rawQuery(query, null);

        if (c.getCount() == 0) {
            res = null;
        } else {
            while (c.moveToNext()) {
                res.add(new Product(c.getInt(0), c.getString(1), c.getLong(2), getCategory("id_category=" + c.getInt(3)).getName(), c.getFloat(4), c.getFloat(5)));
            }
        }

        return res;
    }

    public Product getProduct(String where){
        Product res;

        String query = "SELECT * FROM products";
        if(where != null && !where.equals("")){
            query += " WHERE " + where;
        }
        query += " LIMIT 1";

        Cursor c = myDb.rawQuery(query, null);

        if(c.moveToNext()){
            res = new Product(c.getInt(0), c.getString(1), c.getLong(2), getCategory("id_category=" + c.getInt(3)).getName(), c.getFloat(4), c.getFloat(5));
        }else{
            res = null;
        }

        return res;
    }

    //Type = -1 for normal price OR 1 for sale price
    public void updatePrice(Integer idProduct, Integer type){
        Float res;

        String query = "SELECT Price FROM Purchases WHERE Id_product='" + idProduct + "' AND sale = " + type;

        Cursor c = myDb.rawQuery(query, null);

        Float sum = 0f;
        Integer i = 0;
        if(c.getCount() != 0){
            while(c.moveToNext()){
                i++;
                sum += c.getFloat(0);
            }
            res = sum/i;
            myDb.execSQL("UPDATE Products SET " + (type==1 ? "Sale_price" : "Price") + "=" + MyFunctions.priceConverter(res.toString()) + " WHERE Id_product='" + idProduct + "'");
        }
    }

    public void updateNormalPrice(Integer idProduct){
        updatePrice(idProduct, -1);
    }

    public void updateSalePrice(Integer idProduct){
        updatePrice(idProduct, 1);
    }

    public List<Store> getStores(){
        List<Store> res = new ArrayList<>();
        String query = "SELECT * FROM stores";
        Cursor c = myDb.rawQuery(query, null);
        if (c.getCount() == 0) {
            res = null;
        } else {
            while (c.moveToNext()){
                res.add(new Store(c.getInt(0), c.getString(1)));
            }
        }

        return res;
    }

    public Store getStore(String where){
        Store res;

        String query = "SELECT * FROM stores";
        if(where != null && !where.equals("")){
            query += " WHERE " + where;
        }
        query += " LIMIT 1";

        Cursor c = myDb.rawQuery(query, null);

        if(c.moveToNext()){
            res = new Store(c.getInt(0), c.getString(1));
        }else{
            res = null;
        }

        return res;
    }

    public Integer getStoreId(String name){
        String query = "SELECT Id_store FROM Stores WHERE name='" + name + "'";
        Cursor c = myDb.rawQuery(query, null);
        Integer res;
        if(c.getCount() == 0){
            res = null;
        }else {
            c.moveToNext();
            res = c.getInt(0);
        }

        return res;
    }

    public List<Category> getCategories() {
        List<Category> res = new ArrayList<>();
        String query = "SELECT * FROM categories";
        Cursor c = myDb.rawQuery(query, null);
        if (c.getCount() == 0) {
            res = null;
        } else {
            while (c.moveToNext()) {
                res.add(new Category(c.getInt(0), c.getString(1)));
            }
        }

        return res;
    }

    public Category getCategory(String where){
        Category res;

        String query = "SELECT * FROM categories";
        if(where != null){
            query += " WHERE " + where;
        }
        query += " LIMIT 1";

        Cursor c = myDb.rawQuery(query, null);

        if(c.moveToNext()){
            res = new Category(c.getInt(0), c.getString(1));
        }else{
            res = null;
        }

        return res;
    }

    public String getCategoryName(Integer id) {
        String query = "SELECT Name FROM Categories WHERE Id_category='" + 1 + "'";
        Cursor c = myDb.rawQuery(query, null);
        String res;
        if(c.getCount() == 0){
            res = null;
        }else{
            c.moveToNext();
            res = c.getString(0);
        }

        return res;
    }

    public Integer getCategoryId(String name){
        String query = "SELECT Id_category FROM Categories WHERE name='" + name + "'";
        Cursor c = myDb.rawQuery(query, null);
        Integer res;
        if(c.getCount() == 0){
            res = null;
        }else {
            c.moveToNext();
            res = c.getInt(0);
        }

        return res;
    }

    public List<Purchase> getPurchases(String where, String orderBy){
        return getPurchases(null, where, orderBy, null);
    }

    public List<Purchase> getPurchases(String select, String where, String orderBy, Integer limit) {

        List<Purchase> res = new ArrayList<>();

        String query = "SELECT ";

        if(select != null && !select.equals("")){
            query += select;
        }else{
            query += "*";
        }

        query += " FROM Purchases";

        if(where != null && !where.equals("")){
            query += " WHERE " + where;
        }

        if(orderBy != null && !orderBy.equals("")){
            query += " ORDER BY " + orderBy;
        }

        if(limit != null && !limit.equals("")){
            query += " LIMIT " + limit;
        }

        Cursor c = myDb.rawQuery(query, null);

        if (c.getCount() == 0) {
            res = null;
        } else {
            while (c.moveToNext()) {
                res.add(new Purchase(c.getInt(0), c.getFloat(1), c.getInt(2), c.getInt(3), c.getInt(4)>0 ? true : false, c.getString(5)));
            }
        }

        return res;
    }

    public Integer getMostFrequentlyProduct(){
        return getMostFrequently("Id_product");
    }

    public Integer getMostFrequentlyStore(){
        return getMostFrequently("Id_store");
    }

    public Integer getMostFrequently(String s){
        String query = "SELECT " + s + ", COUNT(" + s + ") as count FROM Purchases GROUP BY " + s + " ORDER BY count DESC LIMIT 1";
        Cursor c = myDb.rawQuery(query, null);
        Integer res;
        if(c.getCount() == 0){
            res = null;
        }else {
            c.moveToNext();
            res = c.getInt(0);
        }

        return res;
    }

    public Integer getMinYear(){
        return getYear("asc");
    }

    public Integer getMaxYear(){
        return getYear("desc");
    }

    private Integer getYear(String s){
        Integer res;
        String d;
        String query = "SELECT date FROM purchases ORDER BY date " + s + " LIMIT 1";
        Cursor c = myDb.rawQuery(query, null);
        if(c.getCount() == 0){
            res = null;
        }else{
            c.moveToNext();
            d = c.getString(0);
            Date date = new Date(context, d);
            res = date.getYear();
        }

        return res;
    }

    public List<String> getDates(String where){
        String query = "SELECT DISTINCT Date FROM Purchases";

        if(where != null && !where.equals("")){
            query += " WHERE " + where;
        }

        query += " ORDER BY date desc";

        Log.i("Query", query);

        Cursor c = myDb.rawQuery(query, null);
        List<String> res = new ArrayList<>();

        if(c.getCount() == 0){
            res = null;
        }else {
            while (c.moveToNext()) {
                res.add(c.getString(0));
            }
        }

        return res;
    }

    public List<String> getNamesList(String table){
        List<String> res = new ArrayList<>();
        String query = "SELECT DISTINCT Name FROM " + table;
        Cursor c = myDb.rawQuery(query, null);
        if(c.getCount() == 0){
            res = null;
        } else {
            while (c.moveToNext()){
                res.add(c.getString(0));
            }
        }

        return res;
    }

    public List<Long> getBarcodesList(){
        List<Long> res = new ArrayList<>();
        String query = "SELECT Bar_code FROM products";
        Cursor c = myDb.rawQuery(query, null);
        if(c.getCount() == 0){
            res = null;
        }else{
            while(c.moveToNext()){
                res.add(c.getLong(0));
            }
        }

        return res;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Products");
        db.execSQL("DELETE FROM Stores");
        db.execSQL("DELETE FROM Categories");
        db.execSQL("DELETE FROM Purchases");
    }

    public boolean checkIfStoreExists(String name) {
        List<Store> storesList = getStores();
        if(storesList != null){
            for(Store s : storesList){
                if(s.getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }

}
