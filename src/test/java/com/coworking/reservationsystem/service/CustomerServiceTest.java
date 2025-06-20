package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.CustomerDto;
import com.coworking.reservationsystem.model.entity.Customer;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.CustomerRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;

    private Tenant testTenant;
    private Customer testCustomer;
    private CustomerDto testCustomerDto;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setName("Test Tenant");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setPhone("0712345678");
        testCustomer.setEmail("john@example.com");
        testCustomer.setNotes("VIP");
        testCustomer.setTenant(testTenant);
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());

        testCustomerDto = new CustomerDto();
        testCustomerDto.setId(1L);
        testCustomerDto.setFirstName("John");
        testCustomerDto.setLastName("Doe");
        testCustomerDto.setPhone("0712345678");
        testCustomerDto.setEmail("john@example.com");
        testCustomerDto.setNotes("VIP");
        testCustomerDto.setTenantId(1L);
        testCustomerDto.setCreatedAt(LocalDateTime.now());
        testCustomerDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createCustomer_Valid_ReturnsCustomer() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.existsByPhoneAndTenantId("0712345678", 1L)).thenReturn(false);
        when(customerRepository.existsByEmailAndTenantId("john@example.com", 1L)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDto result = customerService.createCustomer(testCustomerDto);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_TenantNotFound_ThrowsResourceNotFoundException() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.createCustomer(testCustomerDto));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_PhoneExists_ThrowsValidationException() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.existsByPhoneAndTenantId("0712345678", 1L)).thenReturn(true);
        assertThrows(ValidationException.class, () -> customerService.createCustomer(testCustomerDto));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_EmailExists_ThrowsValidationException() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.existsByPhoneAndTenantId("0712345678", 1L)).thenReturn(false);
        when(customerRepository.existsByEmailAndTenantId("john@example.com", 1L)).thenReturn(true);
        assertThrows(ValidationException.class, () -> customerService.createCustomer(testCustomerDto));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getCustomerById_Existing_ReturnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        CustomerDto result = customerService.getCustomerById(1L);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getCustomerById_NotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    @Test
    void getAllCustomersByTenant_ReturnsList() {
        when(customerRepository.findByTenantIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(testCustomer));
        List<CustomerDto> result = customerService.getAllCustomersByTenant(1L);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void updateCustomer_Valid_ReturnsUpdated() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        CustomerDto result = customerService.updateCustomer(1L, testCustomerDto);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void updateCustomer_NotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(1L, testCustomerDto));
    }

    @Test
    void updateCustomer_TenantNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(1L, testCustomerDto));
    }

    @Test
    void updateCustomer_PhoneExists_ThrowsValidationException() {
        CustomerDto updateDto = new CustomerDto();
        updateDto.setId(1L);
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setPhone("0712345679"); // Different phone
        updateDto.setEmail("john@example.com");
        updateDto.setNotes("VIP");
        updateDto.setTenantId(1L);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.existsByPhoneAndTenantId("0712345679", 1L)).thenReturn(true);
        assertThrows(ValidationException.class, () -> customerService.updateCustomer(1L, updateDto));
    }

    @Test
    void updateCustomer_EmailExists_ThrowsValidationException() {
        CustomerDto updateDto = new CustomerDto();
        updateDto.setId(1L);
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setPhone("0712345678");
        updateDto.setEmail("jane@example.com"); // Different email
        updateDto.setNotes("VIP");
        updateDto.setTenantId(1L);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(customerRepository.existsByEmailAndTenantId("jane@example.com", 1L)).thenReturn(true);
        assertThrows(ValidationException.class, () -> customerService.updateCustomer(1L, updateDto));
    }

    @Test
    void deleteCustomer_Existing_Deletes() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);
        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(1L));
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void searchCustomersByName_ReturnsList() {
        when(customerRepository.findByTenantIdAndNameContainingIgnoreCase(1L, "John")).thenReturn(Arrays.asList(testCustomer));
        List<CustomerDto> result = customerService.searchCustomersByName(1L, "John");
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void searchCustomersByPhone_ReturnsList() {
        when(customerRepository.findByTenantIdAndPhoneContaining(1L, "0712")).thenReturn(Arrays.asList(testCustomer));
        List<CustomerDto> result = customerService.searchCustomersByPhone(1L, "0712");
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void findByPhone_Existing_ReturnsCustomer() {
        when(customerRepository.findByPhoneAndTenantId("0712345678", 1L)).thenReturn(Optional.of(testCustomer));
        CustomerDto result = customerService.findByPhone(1L, "0712345678");
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void findByPhone_NotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findByPhoneAndTenantId("0712345678", 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.findByPhone(1L, "0712345678"));
    }

    @Test
    void findByEmail_Existing_ReturnsCustomer() {
        when(customerRepository.findByEmailAndTenantId("john@example.com", 1L)).thenReturn(Optional.of(testCustomer));
        CustomerDto result = customerService.findByEmail(1L, "john@example.com");
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void findByEmail_NotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findByEmailAndTenantId("john@example.com", 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.findByEmail(1L, "john@example.com"));
    }

    @Test
    void existsByPhone_ReturnsBoolean() {
        when(customerRepository.existsByPhoneAndTenantId("0712345678", 1L)).thenReturn(true);
        assertTrue(customerService.existsByPhone(1L, "0712345678"));
    }

    @Test
    void existsByEmail_ReturnsBoolean() {
        when(customerRepository.existsByEmailAndTenantId("john@example.com", 1L)).thenReturn(true);
        assertTrue(customerService.existsByEmail(1L, "john@example.com"));
    }

    @Test
    void getCustomerCountByTenant_ReturnsCount() {
        when(customerRepository.countByTenantId(1L)).thenReturn(5L);
        assertEquals(5L, customerService.getCustomerCountByTenant(1L));
    }
} 