package by.roman.radiomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.Settings;

@Component
public interface SettingsRepository extends JpaRepository<Settings,Long>{
    Settings findSettingsById(Long id);
}