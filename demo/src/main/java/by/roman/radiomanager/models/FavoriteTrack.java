package by.roman.radiomanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_favorites_track")
public class FavoriteTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(columnDefinition = "TEXT")
    private String track;

    public FavoriteTrack(Long userId, String track) {
        this.userId = userId;
        this.track = track;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTrack() {
        return track;
    }
}
