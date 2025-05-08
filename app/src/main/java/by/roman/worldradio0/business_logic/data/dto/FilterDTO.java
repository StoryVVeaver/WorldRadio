package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.Filter;

public class FilterDTO {
    private int id;
    private String name;
    private String codec;
    private String country;
    private String tags;
    private String lang;
    private int sort;
    public Filter toModel(){
        return new Filter(id,name,codec,country, tags,lang,sort);
    }
    public FilterDTO fromModel(Filter filter){
        FilterDTO dto = new FilterDTO();
        dto.id = filter.getId();
        dto.name = filter.getName();
        dto.codec = filter.getCodec();
        dto.country = filter.getCountry();
        dto.tags = filter.getTag();
        dto.lang = filter.getLang();
        dto.sort = filter.getSort();
        return dto;
    }
    public int getId() {
        return id;
    }
    public String getCountry() {
        return country;
    }
    public String getTags() {
        return tags;
    }
    public String getLang() {
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
}
