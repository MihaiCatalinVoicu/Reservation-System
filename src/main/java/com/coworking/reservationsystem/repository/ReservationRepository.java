package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    List<Reservation> findBySpaceId(Long spaceId);
    Page<Reservation> findBySpaceId(Long spaceId, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.space.id = :spaceId " +
           "AND ((r.startTime BETWEEN :startTime AND :endTime) " +
           "OR (r.endTime BETWEEN :startTime AND :endTime) " +
           "OR (:startTime BETWEEN r.startTime AND r.endTime))")
    List<Reservation> findOverlappingReservations(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
