package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_favorites")
public class Favorites {
    @Id
    private Long id;
    private String uuid;

    public Favorites(Long id, String uuid) {
        this.id = id;
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }
}
