package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_filters")
public class Filters {
    @Id
    private Long id;
    private String country;
    private String tags;
    private String lang;
    private int sort;

    public Filters(Long id, String country, String tags, String lang, int sort) {
        this.id = id;
        this.country = country;
        this.tags = tags;
        this.lang = lang;
        this.sort = sort;
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
