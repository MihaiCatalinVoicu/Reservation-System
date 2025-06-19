package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find all customers by tenant ID
     */
    List<Customer> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    
    /**
     * Find customer by phone number and tenant ID
     */
    Optional<Customer> findByPhoneAndTenantId(String phone, Long tenantId);
    
    /**
     * Find customer by email and tenant ID
     */
    Optional<Customer> findByEmailAndTenantId(String email, Long tenantId);
    
    /**
     * Find customers by name (first name or last name contains the search term)
     */
    @Query("SELECT c FROM Customer c WHERE c.tenant.id = :tenantId AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Customer> findByTenantIdAndNameContainingIgnoreCase(@Param("tenantId") Long tenantId, 
                                                           @Param("searchTerm") String searchTerm);
    
    /**
     * Find customers by phone number containing the search term
     */
    @Query("SELECT c FROM Customer c WHERE c.tenant.id = :tenantId AND " +
           "c.phone LIKE CONCAT('%', :searchTerm, '%') " +
           "ORDER BY c.createdAt DESC")
    List<Customer> findByTenantIdAndPhoneContaining(@Param("tenantId") Long tenantId, 
                                                   @Param("searchTerm") String searchTerm);
    
    /**
     * Check if customer exists by phone and tenant
     */
    boolean existsByPhoneAndTenantId(String phone, Long tenantId);
    
    /**
     * Check if customer exists by email and tenant
     */
    boolean existsByEmailAndTenantId(String email, Long tenantId);
    
    /**
     * Count customers by tenant
     */
    long countByTenantId(Long tenantId);
} 