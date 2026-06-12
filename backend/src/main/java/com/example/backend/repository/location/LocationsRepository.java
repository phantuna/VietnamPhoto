package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationsRepository extends JpaRepository<Locations, String>,LocationsRepositoryCustom {
    Optional<Locations> findByCode(String code);

    Optional<Locations> findFirstByNameWithTypeContainingAndLevel(String nameWithType, Integer level);
    
    @Query(value = "SELECT l FROM Locations l WHERE l.deleted = :deleted", 
           countQuery = "SELECT count(l) FROM Locations l WHERE l.deleted = :deleted")
    org.springframework.data.domain.Page<Locations> findByDeleted(@Param("deleted") int deleted, org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT l FROM Locations l WHERE l.deleted = :deleted AND l.level = :level", 
           countQuery = "SELECT count(l) FROM Locations l WHERE l.deleted = :deleted AND l.level = :level")
    org.springframework.data.domain.Page<Locations> findByDeletedAndLevel(@Param("deleted") int deleted, @Param("level") Integer level, org.springframework.data.domain.Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE locations SET deleted = :deleted WHERE id = :locationId", nativeQuery = true)
    void toggleLocationStatus(@Param("locationId") String locationId, @Param("deleted") int deleted);
}
