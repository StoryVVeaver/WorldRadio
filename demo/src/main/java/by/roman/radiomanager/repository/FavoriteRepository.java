package by.roman.radiomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.Favorites;

@Component
public interface FavoriteRepository extends JpaRepository<Favorites, Long>{
    List<Favorites> findByUserId(Long userId);
}
