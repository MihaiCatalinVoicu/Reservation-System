package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    
    /**
     * Find all tables by tenant ID
     */
    List<RestaurantTable> findByTenantIdOrderByName(Long tenantId);
    
    /**
     * Find all tables by space ID
     */
    List<RestaurantTable> findBySpaceIdOrderByName(Long spaceId);
    
    /**
     * Find all tables by space ID and tenant ID
     */
    List<RestaurantTable> findBySpaceIdAndTenantIdOrderByName(Long spaceId, Long tenantId);
    
    /**
     * Find all tables by status and tenant ID
     */
    List<RestaurantTable> findByStatusAndTenantId(RestaurantTable.TableStatus status, Long tenantId);
    
    /**
     * Find all available tables by tenant ID
     */
    List<RestaurantTable> findByStatusAndTenantIdOrderByName(RestaurantTable.TableStatus status, Long tenantId);
    
    /**
     * Find tables by number of seats (minimum capacity) and tenant ID
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.tenant.id = :tenantId AND t.numberOfSeats >= :minSeats AND t.status = 'AVAILABLE' ORDER BY t.numberOfSeats ASC")
    List<RestaurantTable> findAvailableTablesByMinSeats(@Param("tenantId") Long tenantId, @Param("minSeats") Integer minSeats);
    
    /**
     * Find table by ID and tenant ID
     */
    Optional<RestaurantTable> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * Check if table exists by name and tenant ID
     */
    boolean existsByNameAndTenantId(String name, Long tenantId);
    
    /**
     * Count tables by tenant ID
     */
    long countByTenantId(Long tenantId);
    
    /**
     * Count tables by status and tenant ID
     */
    long countByStatusAndTenantId(RestaurantTable.TableStatus status, Long tenantId);
    
    /**
     * Find all tables by status (legacy method)
     */
    List<RestaurantTable> findByStatusOrderByName(RestaurantTable.TableStatus status);
    
    /**
     * Find available tables by minimum seats (legacy method)
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.numberOfSeats >= :minSeats AND t.status = 'AVAILABLE' ORDER BY t.numberOfSeats ASC")
    List<RestaurantTable> findAvailableTablesByMinSeats(@Param("minSeats") Integer minSeats);
} 