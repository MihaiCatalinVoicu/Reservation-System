package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByTenantId(Long tenantId);
}
