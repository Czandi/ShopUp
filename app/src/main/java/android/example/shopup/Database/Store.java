package android.example.shopup.Database;

public class Store {

    private Integer id;
    private String name;

    public Store(Integer id, String name){
        setId(id);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
