package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.CustomerDto;
import com.coworking.reservationsystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * Create a new customer
     */
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto createdCustomer = customerService.createCustomer(customerDto);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        try {
            CustomerDto customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all customers for a tenant
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<CustomerDto>> getAllCustomersByTenant(@PathVariable Long tenantId) {
        List<CustomerDto> customers = customerService.getAllCustomersByTenant(tenantId);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Update customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, 
                                                    @Valid @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Search customers by name
     */
    @GetMapping("/tenant/{tenantId}/search/name")
    public ResponseEntity<List<CustomerDto>> searchCustomersByName(@PathVariable Long tenantId,
                                                                  @RequestParam String searchTerm) {
        List<CustomerDto> customers = customerService.searchCustomersByName(tenantId, searchTerm);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Search customers by phone
     */
    @GetMapping("/tenant/{tenantId}/search/phone")
    public ResponseEntity<List<CustomerDto>> searchCustomersByPhone(@PathVariable Long tenantId,
                                                                   @RequestParam String searchTerm) {
        List<CustomerDto> customers = customerService.searchCustomersByPhone(tenantId, searchTerm);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Find customer by phone number
     */
    @GetMapping("/tenant/{tenantId}/phone/{phone}")
    public ResponseEntity<CustomerDto> findByPhone(@PathVariable Long tenantId, 
                                                  @PathVariable String phone) {
        try {
            CustomerDto customer = customerService.findByPhone(tenantId, phone);
            return ResponseEntity.ok(customer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Find customer by email
     */
    @GetMapping("/tenant/{tenantId}/email/{email}")
    public ResponseEntity<CustomerDto> findByEmail(@PathVariable Long tenantId, 
                                                  @PathVariable String email) {
        try {
            CustomerDto customer = customerService.findByEmail(tenantId, email);
            return ResponseEntity.ok(customer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Check if customer exists by phone
     */
    @GetMapping("/tenant/{tenantId}/exists/phone/{phone}")
    public ResponseEntity<Boolean> existsByPhone(@PathVariable Long tenantId, 
                                                @PathVariable String phone) {
        boolean exists = customerService.existsByPhone(tenantId, phone);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Check if customer exists by email
     */
    @GetMapping("/tenant/{tenantId}/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable Long tenantId, 
                                                @PathVariable String email) {
        boolean exists = customerService.existsByEmail(tenantId, email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Get customer count by tenant
     */
    @GetMapping("/tenant/{tenantId}/count")
    public ResponseEntity<Long> getCustomerCountByTenant(@PathVariable Long tenantId) {
        long count = customerService.getCustomerCountByTenant(tenantId);
        return ResponseEntity.ok(count);
    }
} 