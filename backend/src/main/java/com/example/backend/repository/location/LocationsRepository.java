package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationsRepository extends JpaRepository<Locations, UUID> {
    Optional<Locations> findByCode(String code);
}
