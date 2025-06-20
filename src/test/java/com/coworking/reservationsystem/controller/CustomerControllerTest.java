package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.CustomerDto;
import com.coworking.reservationsystem.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDto testCustomerDto;
    private List<CustomerDto> testCustomers;

    @BeforeEach
    void setUp() {
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

        testCustomers = Arrays.asList(testCustomerDto);
    }

    @Test
    void createCustomer_ValidCustomer_ReturnsCreatedCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(testCustomerDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phone").value("0712345678"));

        verify(customerService).createCustomer(any(CustomerDto.class));
    }

    @Test
    void createCustomer_InvalidCustomer_ReturnsBadRequest() throws Exception {
        CustomerDto invalidCustomer = new CustomerDto();
        invalidCustomer.setFirstName(""); // Invalid - empty first name
        invalidCustomer.setPhone("123"); // Invalid phone format

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void getCustomerById_ExistingCustomer_ReturnsCustomer() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(testCustomerDto);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    void getCustomerById_NonExistentCustomer_ReturnsNotFound() throws Exception {
        when(customerService.getCustomerById(999L))
                .thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomerById(999L);
    }

    @Test
    void getAllCustomersByTenant_ReturnsCustomersList() throws Exception {
        when(customerService.getAllCustomersByTenant(1L)).thenReturn(testCustomers);

        mockMvc.perform(get("/api/v1/customers/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(customerService).getAllCustomersByTenant(1L);
    }

    @Test
    void updateCustomer_ValidCustomer_ReturnsUpdatedCustomer() throws Exception {
        CustomerDto updatedCustomer = new CustomerDto();
        updatedCustomer.setId(1L);
        updatedCustomer.setFirstName("Jane");
        updatedCustomer.setLastName("Smith");
        updatedCustomer.setPhone("0712345678");
        updatedCustomer.setEmail("jane@example.com");
        updatedCustomer.setNotes("Updated notes");
        updatedCustomer.setTenantId(1L);

        when(customerService.updateCustomer(eq(1L), any(CustomerDto.class))).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(customerService).updateCustomer(eq(1L), any(CustomerDto.class));
    }

    @Test
    void updateCustomer_NonExistentCustomer_ReturnsNotFound() throws Exception {
        when(customerService.updateCustomer(eq(999L), any(CustomerDto.class)))
                .thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDto)))
                .andExpect(status().isNotFound());

        verify(customerService).updateCustomer(eq(999L), any(CustomerDto.class));
    }

    @Test
    void deleteCustomer_ExistingCustomer_ReturnsNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_NonExistentCustomer_ReturnsNotFound() throws Exception {
        doThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(999L);

        mockMvc.perform(delete("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService).deleteCustomer(999L);
    }

    @Test
    void searchCustomersByName_ReturnsCustomersList() throws Exception {
        when(customerService.searchCustomersByName(1L, "John")).thenReturn(testCustomers);

        mockMvc.perform(get("/api/v1/customers/tenant/1/search/name")
                        .param("searchTerm", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(customerService).searchCustomersByName(1L, "John");
    }

    @Test
    void searchCustomersByPhone_ReturnsCustomersList() throws Exception {
        when(customerService.searchCustomersByPhone(1L, "0712")).thenReturn(testCustomers);

        mockMvc.perform(get("/api/v1/customers/tenant/1/search/phone")
                        .param("searchTerm", "0712"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phone").value("0712345678"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(customerService).searchCustomersByPhone(1L, "0712");
    }

    @Test
    void findByPhone_ExistingCustomer_ReturnsCustomer() throws Exception {
        when(customerService.findByPhone(1L, "0712345678")).thenReturn(testCustomerDto);

        mockMvc.perform(get("/api/v1/customers/tenant/1/phone/0712345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("0712345678"));

        verify(customerService).findByPhone(1L, "0712345678");
    }

    @Test
    void findByPhone_NonExistentCustomer_ReturnsNotFound() throws Exception {
        when(customerService.findByPhone(1L, "9999999999"))
                .thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/tenant/1/phone/9999999999"))
                .andExpect(status().isNotFound());

        verify(customerService).findByPhone(1L, "9999999999");
    }

    @Test
    void findByEmail_ExistingCustomer_ReturnsCustomer() throws Exception {
        when(customerService.findByEmail(1L, "john@example.com")).thenReturn(testCustomerDto);

        mockMvc.perform(get("/api/v1/customers/tenant/1/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(customerService).findByEmail(1L, "john@example.com");
    }

    @Test
    void findByEmail_NonExistentCustomer_ReturnsNotFound() throws Exception {
        when(customerService.findByEmail(1L, "nonexistent@example.com"))
                .thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/tenant/1/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());

        verify(customerService).findByEmail(1L, "nonexistent@example.com");
    }

    @Test
    void existsByPhone_ExistingPhone_ReturnsTrue() throws Exception {
        when(customerService.existsByPhone(1L, "0712345678")).thenReturn(true);

        mockMvc.perform(get("/api/v1/customers/tenant/1/exists/phone/0712345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(customerService).existsByPhone(1L, "0712345678");
    }

    @Test
    void existsByPhone_NonExistentPhone_ReturnsFalse() throws Exception {
        when(customerService.existsByPhone(1L, "9999999999")).thenReturn(false);

        mockMvc.perform(get("/api/v1/customers/tenant/1/exists/phone/9999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(customerService).existsByPhone(1L, "9999999999");
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() throws Exception {
        when(customerService.existsByEmail(1L, "john@example.com")).thenReturn(true);

        mockMvc.perform(get("/api/v1/customers/tenant/1/exists/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(customerService).existsByEmail(1L, "john@example.com");
    }

    @Test
    void existsByEmail_NonExistentEmail_ReturnsFalse() throws Exception {
        when(customerService.existsByEmail(1L, "nonexistent@example.com")).thenReturn(false);

        mockMvc.perform(get("/api/v1/customers/tenant/1/exists/email/nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(customerService).existsByEmail(1L, "nonexistent@example.com");
    }

    @Test
    void getCustomerCountByTenant_ReturnsCount() throws Exception {
        when(customerService.getCustomerCountByTenant(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/customers/tenant/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(customerService).getCustomerCountByTenant(1L);
    }
} 