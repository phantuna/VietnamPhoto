package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationsRepository extends JpaRepository<Locations, String>,LocationsRepositoryCustom {
    Optional<Locations> findByCode(String code);

    Optional<Locations> findFirstByNameWithTypeContainingAndLevel(String nameWithType, Integer level);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM locations WHERE level = 2", 
            countQuery = "SELECT count(*) FROM locations WHERE level = 2", 
            nativeQuery = true)
    org.springframework.data.domain.Page<Locations> findAllLocationsIncludeDeleted(org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(value = "UPDATE locations SET deleted = :deleted WHERE id = :locationId", nativeQuery = true)
    void toggleLocationStatus(@org.springframework.data.repository.query.Param("locationId") String locationId, @org.springframework.data.repository.query.Param("deleted") int deleted);
}
