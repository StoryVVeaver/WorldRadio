package by.roman.worldradio0.business_logic.data.models;

public class Filter {
    private final int id;
    private String name;
    private String codec;
    private String country;
    private String tag;
    private String lang;
    private int sort;

    public Filter(int id,String name,String codec, String country,String tag,String lang,int sort){
        this.id = id;
        this.name = name;
        this.codec = codec;
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
    public String getName() {
        return name;
    }
    public String getCodec() {
        return codec;
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
    public void setName(String name) {
        this.name = name;
    }
    public void setCodec(String codec) {
        this.codec = codec;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
}
