package com.example.backend.repository.photo;

import com.example.backend.entity.Photos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhotosRepository extends JpaRepository<Photos, String>,PhotosRepositoryCustom {

}
