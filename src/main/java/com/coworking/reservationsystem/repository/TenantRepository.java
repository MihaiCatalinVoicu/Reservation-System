package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findBySubdomain(String subdomain);
    
    Optional<Tenant> findByName(String name);
    
    List<Tenant> findByStatus(Tenant.TenantStatus status);
    
    List<Tenant> findByPlan(Tenant.TenantPlan plan);
    
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionEndDate < :date AND t.status = 'ACTIVE'")
    List<Tenant> findExpiredSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionEndDate BETWEEN :startDate AND :endDate")
    List<Tenant> findSubscriptionsExpiringBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    boolean existsBySubdomain(String subdomain);
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.status = 'ACTIVE'")
    long countActiveTenants();
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.plan = :plan")
    long countByPlan(@Param("plan") Tenant.TenantPlan plan);
} 