package by.roman.radiomanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.User;

@Component
public interface UserRepository extends JpaRepository<User, Long>{
    User findByLogin(String login);
    User findByLoginAndPassword(String login, String password);
}
