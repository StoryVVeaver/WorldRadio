package by.roman.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.roman.demo.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
