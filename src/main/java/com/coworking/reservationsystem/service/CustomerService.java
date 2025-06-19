package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.CustomerDto;
import java.util.List;

public interface CustomerService {
    
    /**
     * Create a new customer
     */
    CustomerDto createCustomer(CustomerDto customerDto);
    
    /**
     * Get customer by ID
     */
    CustomerDto getCustomerById(Long id);
    
    /**
     * Get all customers for a tenant
     */
    List<CustomerDto> getAllCustomersByTenant(Long tenantId);
    
    /**
     * Update customer
     */
    CustomerDto updateCustomer(Long id, CustomerDto customerDto);
    
    /**
     * Delete customer
     */
    void deleteCustomer(Long id);
    
    /**
     * Search customers by name
     */
    List<CustomerDto> searchCustomersByName(Long tenantId, String searchTerm);
    
    /**
     * Search customers by phone
     */
    List<CustomerDto> searchCustomersByPhone(Long tenantId, String searchTerm);
    
    /**
     * Find customer by phone number
     */
    CustomerDto findByPhone(Long tenantId, String phone);
    
    /**
     * Find customer by email
     */
    CustomerDto findByEmail(Long tenantId, String email);
    
    /**
     * Check if customer exists by phone
     */
    boolean existsByPhone(Long tenantId, String phone);
    
    /**
     * Check if customer exists by email
     */
    boolean existsByEmail(Long tenantId, String email);
    
    /**
     * Get customer count by tenant
     */
    long getCustomerCountByTenant(Long tenantId);
} 