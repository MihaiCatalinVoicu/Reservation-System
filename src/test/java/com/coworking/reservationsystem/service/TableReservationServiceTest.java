package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.TableReservationDto;
import com.coworking.reservationsystem.model.entity.Customer;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.model.entity.TableReservation;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.CustomerRepository;
import com.coworking.reservationsystem.repository.RestaurantTableRepository;
import com.coworking.reservationsystem.repository.TableReservationRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.TableReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableReservationServiceTest {

    @Mock
    private TableReservationRepository reservationRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TableReservationServiceImpl reservationService;

    private TableReservation testReservation;
    private TableReservationDto testReservationDto;
    private RestaurantTable testTable;
    private Customer testCustomer;
    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setName("Test Restaurant");

        testTable = new RestaurantTable();
        testTable.setId(1L);
        testTable.setName("Test Table");
        testTable.setNumberOfSeats(4);
        testTable.setTenant(testTenant);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setPhone("+40712345678");
        testCustomer.setTenant(testTenant);

        LocalDateTime requestedTime = LocalDateTime.now().plusHours(2);
        LocalDateTime estimatedArrivalTime = LocalDateTime.now().plusHours(2).plusMinutes(15);

        testReservation = new TableReservation();
        testReservation.setId(1L);
        testReservation.setTable(testTable);
        testReservation.setCustomer(testCustomer);
        testReservation.setNumberOfPeople(4);
        testReservation.setRequestedTime(requestedTime);
        testReservation.setEstimatedArrivalTime(estimatedArrivalTime);
        testReservation.setStatus(TableReservation.TableReservationStatus.PENDING);
        testReservation.setSpecialRequests("Test requests");
        testReservation.setTenant(testTenant);
        testReservation.setCreatedAt(LocalDateTime.now());
        testReservation.setUpdatedAt(LocalDateTime.now());

        testReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                4,
                requestedTime,
                estimatedArrivalTime,
                TableReservation.TableReservationStatus.PENDING,
                "Test requests",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createTableReservation_ValidReservation_ReturnsCreatedReservation() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any())).thenReturn(Arrays.asList());
        when(reservationRepository.save(any(TableReservation.class))).thenReturn(testReservation);

        TableReservationDto result = reservationService.createTableReservation(testReservationDto);

        assertNotNull(result);
        assertEquals(4, result.numberOfPeople());
        assertEquals(TableReservation.TableReservationStatus.PENDING, result.status());
        verify(reservationRepository).save(any(TableReservation.class));
    }

    @Test
    void createTableReservation_TableNotFound_ThrowsResourceNotFoundException() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createTableReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createTableReservation_CustomerNotFound_ThrowsResourceNotFoundException() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createTableReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createTableReservation_TenantNotFound_ThrowsResourceNotFoundException() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createTableReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createTableReservation_OverlappingReservation_ThrowsValidationException() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any())).thenReturn(Arrays.asList(testReservation));

        assertThrows(ValidationException.class, () -> {
            reservationService.createTableReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createTableReservation_PastRequestedTime_ThrowsValidationException() {
        TableReservationDto pastReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                4,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                TableReservation.TableReservationStatus.PENDING,
                "Test requests",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));

        assertThrows(ValidationException.class, () -> {
            reservationService.createTableReservation(pastReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void getTableReservationById_ExistingReservation_ReturnsReservation() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));

        Optional<TableReservationDto> result = reservationService.getTableReservationById(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(4, result.get().numberOfPeople());
    }

    @Test
    void getTableReservationById_NonExistentReservation_ReturnsEmpty() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        Optional<TableReservationDto> result = reservationService.getTableReservationById(1L, 1L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllTableReservationsByTenant_ReturnsReservationsList() {
        List<TableReservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByTenantIdOrderByCreatedAtDesc(1L)).thenReturn(reservations);

        List<TableReservationDto> result = reservationService.getAllTableReservationsByTenant(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).numberOfPeople());
    }

    @Test
    void getTableReservationsByCustomer_ReturnsReservationsList() {
        List<TableReservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByCustomerIdAndTenantIdOrderByCreatedAtDesc(1L, 1L)).thenReturn(reservations);

        List<TableReservationDto> result = reservationService.getTableReservationsByCustomer(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).customerId());
    }

    @Test
    void getPendingTableReservations_ReturnsPendingReservations() {
        List<TableReservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByStatusAndTenantIdOrderByRequestedTimeAsc(TableReservation.TableReservationStatus.PENDING, 1L)).thenReturn(reservations);

        List<TableReservationDto> result = reservationService.getPendingTableReservations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TableReservation.TableReservationStatus.PENDING, result.get(0).status());
    }

    @Test
    void confirmTableReservation_ValidReservation_ReturnsConfirmedReservation() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(TableReservation.class))).thenReturn(testReservation);

        Optional<TableReservationDto> result = reservationService.confirmTableReservation(1L, 1L);

        assertTrue(result.isPresent());
        verify(reservationRepository).save(any(TableReservation.class));
    }

    @Test
    void confirmTableReservation_NonPendingReservation_ThrowsValidationException() {
        testReservation.setStatus(TableReservation.TableReservationStatus.CONFIRMED);
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));

        assertThrows(ValidationException.class, () -> {
            reservationService.confirmTableReservation(1L, 1L);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void rejectTableReservation_ValidReservation_ReturnsRejectedReservation() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(TableReservation.class))).thenReturn(testReservation);

        Optional<TableReservationDto> result = reservationService.rejectTableReservation(1L, 1L);

        assertTrue(result.isPresent());
        verify(reservationRepository).save(any(TableReservation.class));
    }

    @Test
    void cancelTableReservation_ValidReservation_ReturnsCancelledReservation() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(TableReservation.class))).thenReturn(testReservation);

        Optional<TableReservationDto> result = reservationService.cancelTableReservation(1L, 1L);

        assertTrue(result.isPresent());
        verify(reservationRepository).save(any(TableReservation.class));
    }

    @Test
    void completeTableReservation_ValidReservation_ReturnsCompletedReservation() {
        testReservation.setStatus(TableReservation.TableReservationStatus.CONFIRMED);
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(TableReservation.class))).thenReturn(testReservation);

        Optional<TableReservationDto> result = reservationService.completeTableReservation(1L, 1L);

        assertTrue(result.isPresent());
        verify(reservationRepository).save(any(TableReservation.class));
    }

    @Test
    void completeTableReservation_NonConfirmedReservation_ThrowsValidationException() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));

        assertThrows(ValidationException.class, () -> {
            reservationService.completeTableReservation(1L, 1L);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void deleteTableReservation_ExistingReservation_ReturnsTrue() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationRepository).delete(testReservation);

        boolean result = reservationService.deleteTableReservation(1L, 1L);

        assertTrue(result);
        verify(reservationRepository).delete(testReservation);
    }

    @Test
    void deleteTableReservation_NonExistentReservation_ReturnsFalse() {
        when(reservationRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        boolean result = reservationService.deleteTableReservation(1L, 1L);

        assertFalse(result);
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void getTableReservationsByDateRange_ReturnsReservationsList() {
        List<TableReservation> reservations = Arrays.asList(testReservation);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(reservationRepository.findByDateRangeAndTenantId(startDate, endDate, 1L)).thenReturn(reservations);

        List<TableReservationDto> result = reservationService.getTableReservationsByDateRange(startDate, endDate, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTableReservationsByCustomer_WithPagination_ReturnsPage() {
        List<TableReservation> reservations = Arrays.asList(testReservation);
        Page<TableReservation> page = new PageImpl<>(reservations);
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findByCustomerIdAndTenantId(1L, 1L, pageable)).thenReturn(page);

        Page<TableReservationDto> result = reservationService.getTableReservationsByCustomer(1L, 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void hasOverlappingReservations_NoOverlap_ReturnsFalse() {
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any())).thenReturn(Arrays.asList());

        boolean result = reservationService.hasOverlappingReservations(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        assertFalse(result);
    }

    @Test
    void hasOverlappingReservations_HasOverlap_ReturnsTrue() {
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any())).thenReturn(Arrays.asList(testReservation));

        boolean result = reservationService.hasOverlappingReservations(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        assertTrue(result);
    }

    @Test
    void getTableReservationCountByStatus_ReturnsCount() {
        when(reservationRepository.countByStatusAndTenantId(TableReservation.TableReservationStatus.PENDING, 1L)).thenReturn(5L);

        long result = reservationService.getTableReservationCountByStatus(TableReservation.TableReservationStatus.PENDING, 1L);

        assertEquals(5L, result);
    }

    @Test
    void getTableReservationCountByTenant_ReturnsCount() {
        when(reservationRepository.countByTenantId(1L)).thenReturn(10L);

        long result = reservationService.getTableReservationCountByTenant(1L);

        assertEquals(10L, result);
    }
} 