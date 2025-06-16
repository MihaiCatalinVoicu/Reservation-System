package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findBySpaceId(Long spaceId);
    List<Availability> findBySpaceIdAndStartTimeBetween(Long spaceId, LocalDateTime startTime, LocalDateTime endTime);
}
