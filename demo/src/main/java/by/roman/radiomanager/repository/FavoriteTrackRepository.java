package by.roman.radiomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.FavoriteTrack;

@Component
public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long>{
    List<FavoriteTrack> findByUserId(Long userId);
}