package com.example.backend.repository;

import com.example.backend.entity.Photos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhotosRepository extends JpaRepository<Photos, UUID> {

}
