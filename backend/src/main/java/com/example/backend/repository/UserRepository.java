package com.example.backend.repository;

import com.example.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface UserRepository extends JpaRepository<Users, UUID> {
     Users findByEmail(String email);


}
