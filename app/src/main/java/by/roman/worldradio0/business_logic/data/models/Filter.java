package by.roman.worldradio0.business_logic.data.models;

public class Filter {
    private final int id;
    private String country;
    private String tag;
    private String lang;
    private int sort;

    public Filter(int id,String country,String tag,String lang,int sort){
        this.id = id;
        this.country = country;
        this.tag = tag;
        this.lang = lang;
        this.sort = sort;
    }

    // Getters
    public int getId(){
        return id;
    }
    public String getCountry(){
        return country;
    }
    public String getTag(){
        return tag;
    }
    public String getLang(){
        return lang;
    }
    public int getSort() {
        return sort;
    }

    // Setters
    public void setCountry(String country){
        this.country = country;
    }
    public void setTag(String tag){
        this.tag = tag;
    }
    public void setLang(String lang){
        this.lang = lang;
    }
}
