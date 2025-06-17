package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByLocationId(Long locationId);
}
