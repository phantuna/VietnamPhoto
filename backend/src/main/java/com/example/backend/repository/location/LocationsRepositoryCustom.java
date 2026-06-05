package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationsRepositoryCustom {
    Page<Locations> findAllLocationsIncludeDeleted(Pageable pageable);
}
