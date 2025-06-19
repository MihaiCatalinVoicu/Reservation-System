package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.CustomerDto;
import com.coworking.reservationsystem.model.entity.Customer;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.CustomerRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        // Validate tenant exists
        Tenant tenant = tenantRepository.findById(customerDto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + customerDto.getTenantId()));
        
        // Check if customer with same phone already exists for this tenant
        if (customerRepository.existsByPhoneAndTenantId(customerDto.getPhone(), customerDto.getTenantId())) {
            throw new ValidationException("Customer with phone " + customerDto.getPhone() + " already exists for this tenant");
        }
        
        // Check if customer with same email already exists (if email is provided)
        if (customerDto.getEmail() != null && !customerDto.getEmail().trim().isEmpty() &&
            customerRepository.existsByEmailAndTenantId(customerDto.getEmail(), customerDto.getTenantId())) {
            throw new ValidationException("Customer with email " + customerDto.getEmail() + " already exists for this tenant");
        }
        
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setNotes(customerDto.getNotes());
        customer.setTenant(tenant);
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDto(savedCustomer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToDto(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomersByTenant(Long tenantId) {
        List<Customer> customers = customerRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Validate tenant exists
        Tenant tenant = tenantRepository.findById(customerDto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + customerDto.getTenantId()));
        
        // Check if phone is being changed and if it conflicts with existing customer
        if (!customer.getPhone().equals(customerDto.getPhone()) &&
            customerRepository.existsByPhoneAndTenantId(customerDto.getPhone(), customerDto.getTenantId())) {
            throw new ValidationException("Customer with phone " + customerDto.getPhone() + " already exists for this tenant");
        }
        
        // Check if email is being changed and if it conflicts with existing customer
        if (customerDto.getEmail() != null && !customerDto.getEmail().trim().isEmpty() &&
            !customerDto.getEmail().equals(customer.getEmail()) &&
            customerRepository.existsByEmailAndTenantId(customerDto.getEmail(), customerDto.getTenantId())) {
            throw new ValidationException("Customer with email " + customerDto.getEmail() + " already exists for this tenant");
        }
        
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setNotes(customerDto.getNotes());
        customer.setTenant(tenant);
        
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDto(updatedCustomer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomersByName(Long tenantId, String searchTerm) {
        List<Customer> customers = customerRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, searchTerm);
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomersByPhone(Long tenantId, String searchTerm) {
        List<Customer> customers = customerRepository.findByTenantIdAndPhoneContaining(tenantId, searchTerm);
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerDto findByPhone(Long tenantId, String phone) {
        Customer customer = customerRepository.findByPhoneAndTenantId(phone, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with phone: " + phone));
        return convertToDto(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerDto findByEmail(Long tenantId, String email) {
        Customer customer = customerRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return convertToDto(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(Long tenantId, String phone) {
        return customerRepository.existsByPhoneAndTenantId(phone, tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Long tenantId, String email) {
        return customerRepository.existsByEmailAndTenantId(email, tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCustomerCountByTenant(Long tenantId) {
        return customerRepository.countByTenantId(tenantId);
    }
    
    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setNotes(customer.getNotes());
        dto.setTenantId(customer.getTenant().getId());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }
} 