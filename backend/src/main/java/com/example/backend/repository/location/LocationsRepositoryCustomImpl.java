package com.example.backend.repository.location;

import com.example.backend.entity.Locations;
import com.example.backend.entity.QLocations;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class LocationsRepositoryCustomImpl implements LocationsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Locations> findAllLocationsIncludeDeleted(Pageable pageable) {
        QLocations locations = QLocations.locations;

        JPAQuery<Locations> query = queryFactory.selectFrom(locations)
                .where(locations.level.eq(2));

        long total = query.fetchCount();

        List<Locations> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(locations.createdDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
