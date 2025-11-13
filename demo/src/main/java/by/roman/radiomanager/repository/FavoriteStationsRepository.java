package by.roman.radiomanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.FavoriteStation;

@Component
public interface FavoriteStationsRepository extends JpaRepository<FavoriteStation, Long>{
    List<FavoriteStation> findByUserId(Long userId);
}
