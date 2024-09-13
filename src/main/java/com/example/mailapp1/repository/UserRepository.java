package com.example.mailapp1.repository;



import com.example.mailapp1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);
    User findByEmail(String email);

}
