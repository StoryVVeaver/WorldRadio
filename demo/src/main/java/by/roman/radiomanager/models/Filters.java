package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_filters")
public class Filters {
    @Id
    private Long id;
    private String name;
    private String codec;
    private String country;
    private String tags;
    private String lang;
    private int sort;

    public Filters() {
        
    }

    public Filters(Long id, String name, String codec, String country, String tags, String lang, int sort) {
        this.id = id;
        this.name = name;
        this.codec = codec;
        this.country = country;
        this.tags = tags;
        this.lang = lang;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public String getCodec() {
        return codec;
    }

    public Long getId() {
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
}
