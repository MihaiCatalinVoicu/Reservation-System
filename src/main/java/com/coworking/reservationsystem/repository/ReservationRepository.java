package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.dto.Status;
import com.coworking.reservationsystem.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    Page<Reservation> findByCustomerId(Long customerId, Pageable pageable);
    List<Reservation> findBySpaceId(Long spaceId);
    Page<Reservation> findBySpaceId(Long spaceId, Pageable pageable);
    
    // Tenant-based methods
    Optional<Reservation> findByIdAndTenantId(Long id, Long tenantId);
    List<Reservation> findByTenantId(Long tenantId);
    List<Reservation> findByCustomerIdAndTenantId(Long customerId, Long tenantId);
    List<Reservation> findBySpaceIdAndTenantId(Long spaceId, Long tenantId);
    List<Reservation> findByStatusAndTenantId(Status status, Long tenantId);
    
    @Query("SELECT r FROM Reservation r WHERE r.space.id = :spaceId " +
           "AND ((r.startTime BETWEEN :startTime AND :endTime) " +
           "OR (r.endTime BETWEEN :startTime AND :endTime) " +
           "OR (:startTime BETWEEN r.startTime AND r.endTime))")
    List<Reservation> findOverlappingReservations(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.space.id = :spaceId " +
           "AND r.tenant.id = :tenantId " +
           "AND ((r.startTime BETWEEN :startTime AND :endTime) " +
           "OR (r.endTime BETWEEN :startTime AND :endTime) " +
           "OR (:startTime BETWEEN r.startTime AND r.endTime))")
    List<Reservation> findConflictingReservations(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("spaceId") Long spaceId,
            @Param("tenantId") Long tenantId
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.tenant.id = :tenantId " +
           "AND r.startTime >= :startDate AND r.startTime <= :endDate")
    List<Reservation> findByDateRangeAndTenantId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("tenantId") Long tenantId
    );
}
