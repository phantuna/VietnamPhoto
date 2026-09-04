package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationsRepository extends JpaRepository<Locations, String>,LocationsRepositoryCustom {
    Optional<Locations> findByCode(String code);
    Optional<Locations> findFirstByNameWithTypeContainingAndLevel(String nameWithType, Integer level);

    @Query("SELECT l FROM Locations l WHERE l.latitude IS NOT NULL AND l.longitude IS NOT NULL AND l.deleted = 0")
    List<Locations> findAllWithCoordinates();
    
    @Query("SELECT l FROM Locations l WHERE l.deleted = 0 AND l.latitude BETWEEN :minLat AND :maxLat AND l.longitude BETWEEN :minLng AND :maxLng")
    List<Locations> findLocationsWithinBoundingBox(
        @Param("minLat") BigDecimal minLat, 
        @Param("maxLat") BigDecimal maxLat, 
        @Param("minLng") BigDecimal minLng, 
        @Param("maxLng") BigDecimal maxLng
    );
    
    @Query(value = "SELECT l FROM Locations l WHERE l.deleted = :deleted", 
           countQuery = "SELECT count(l) FROM Locations l WHERE l.deleted = :deleted")
    Page<Locations> findByDeleted(@Param("deleted") int deleted, Pageable pageable);

    @Query(value = "SELECT l FROM Locations l WHERE l.deleted = :deleted AND l.level = :level", 
           countQuery = "SELECT count(l) FROM Locations l WHERE l.deleted = :deleted AND l.level = :level")
    Page<Locations> findByDeletedAndLevel(@Param("deleted") int deleted, @Param("level") Integer level, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE locations SET deleted = :deleted WHERE id = :locationId", nativeQuery = true)
    void toggleLocationStatus(@Param("locationId") String locationId, @Param("deleted") int deleted);
}
