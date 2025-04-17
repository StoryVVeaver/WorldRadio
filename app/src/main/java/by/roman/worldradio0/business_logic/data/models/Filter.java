package by.roman.worldradio0.business_logic.data.models;

public class Filter {
    private final int id;
    private final String country;
    private final String style;
    private final String lang;
    private final int sort;

    public Filter(int id,String country,String style,String lang,int sort){
        this.id = id;
        this.country = country;
        this.style = style;
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
        return style;
    }
    public String getLang(){
        return lang;
    }
    public int getSort() {
        return sort;
    }

}
