package by.roman.radiomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.Filters;

@Component
public interface FilterRepository extends JpaRepository<Filters,Long>{
    Filters findFiltersById(Long id);
}
