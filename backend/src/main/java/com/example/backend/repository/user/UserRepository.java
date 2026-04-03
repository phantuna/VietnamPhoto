package com.example.backend.repository.user;

import com.example.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<Users, UUID> {
     Users findByEmail(String email);
     Optional<Users> findByUsername(String username);

}
