package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Уникальный идентификатор записи
    private Long userId;
    private String uuid;

    public Favorites(Long userId, String uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUuid() {
        return uuid;
    }
}
