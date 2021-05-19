package android.example.shopup.Database;

import android.example.shopup.MyFunctions;
import android.util.Log;

import java.util.Date;

public class Product {

    private Integer id;
    private String name;
    private Long barCode;
    private String category;
    private Float price;
    private Float salePrice;

    public Product(Integer id, String name, Long barCode, String category, Float price, Float salePrice){
        setId(id);
        setName(name);
        setCategory(category);
        setBarCode(barCode);
        setPrice(price);
        setSalePrice(salePrice);
    }

    public Float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Float salePrice) {
        this.salePrice = salePrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Long getBarCode() { return barCode; }

    public void setBarCode(Long bar_code) { this.barCode = bar_code; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory(){
        return category;
    }

}
