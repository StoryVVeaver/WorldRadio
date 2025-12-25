package by.roman.radiomanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_filter_stations")
public class FilterStation {

    @Id
    @Column(unique = true)
    private String code;

    public FilterStation() {}

    public FilterStation(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
