package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByLocationId(Long locationId);
    List<Space> findByTenantId(Long tenantId);
    Optional<Space> findByIdAndTenantId(Long id, Long tenantId);
    List<Space> findByLocationIdAndTenantId(Long locationId, Long tenantId);
    List<Space> findByCapacityAndTenantId(Integer capacity, Long tenantId);
    List<Space> findByCapacity(Integer capacity);
}
