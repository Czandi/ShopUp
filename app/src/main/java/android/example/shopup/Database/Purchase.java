package android.example.shopup.Database;

public class Purchase {

    private Integer id;
    private Float price;
    private Integer id_product;
    private Integer id_store;
    private Boolean sale;

    private String date;

    public Purchase(Integer id, Float price, Integer id_product, Integer id_store, Boolean sale, String date){
        setId(id);
        setPrice(price);
        setId_product(id_product);
        setId_store(id_store);
        setSale(sale);
        setDate(date);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public Integer getId_store() {
        return id_store;
    }

    public void setId_store(Integer id_store) {
        this.id_store = id_store;
    }

    public Boolean getSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
