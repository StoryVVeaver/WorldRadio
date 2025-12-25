package by.roman.radiomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.FilterStation;

@Component
public interface FilterStationRepository extends JpaRepository<FilterStation, String>{
}
