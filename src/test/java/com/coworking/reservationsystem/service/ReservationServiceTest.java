package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.ReservationDto;
import com.coworking.reservationsystem.model.dto.Status;
import com.coworking.reservationsystem.model.entity.*;
import com.coworking.reservationsystem.repository.CustomerRepository;
import com.coworking.reservationsystem.repository.ReservationRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.impl.ReservationServiceImpl;
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
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Tenant testTenant;
    private User testUser;
    private Customer testCustomer;
    private Space testSpace;
    private Reservation testReservation;
    private ReservationDto testReservationDto;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setName("Test Tenant");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setTenant(testTenant);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("Jane");
        testCustomer.setLastName("Smith");
        testCustomer.setPhone("+40123456789");
        testCustomer.setTenant(testTenant);

        testSpace = new Space();
        testSpace.setId(1L);
        testSpace.setName("Test Space");
        testSpace.setTenant(testTenant);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);
        LocalDateTime createdAt = LocalDateTime.now();

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setCustomer(testCustomer);
        testReservation.setCreatedByUser(testUser);
        testReservation.setSpace(testSpace);
        testReservation.setStartTime(startTime);
        testReservation.setEndTime(endTime);
        testReservation.setStatus(Status.CONFIRMED);
        testReservation.setTenant(testTenant);

        testReservationDto = new ReservationDto(
                1L,
                1L, // spaceId
                1L, // customerId
                1L, // createdByUserId
                startTime,
                endTime,
                100.0, // totalPrice
                Status.CONFIRMED,
                "Test notes", // notes
                createdAt, // createdAt
                createdAt, // updatedAt
                1L // tenantId
        );
    }

    @Test
    void createReservation_ValidReservation_ReturnsCreatedReservation() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any()))
                .thenReturn(Arrays.asList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationDto result = reservationService.createReservation(testReservationDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_CustomerNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_UserNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_SpaceNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_OverlappingReservation_ThrowsValidationException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(testReservation));

        assertThrows(ValidationException.class, () -> {
            reservationService.createReservation(testReservationDto);
        });

        verify(reservationRepository).findOverlappingReservations(anyLong(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void getReservationById_ExistingReservation_ReturnsReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        ReservationDto result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(Status.CONFIRMED, result.status());
    }

    @Test
    void getReservationById_NonExistentReservation_ThrowsResourceNotFoundException() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.getReservationById(999L);
        });
    }

    @Test
    void getAllReservations_ReturnsReservationsList() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ReservationDto> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getReservationsByCustomerId_ReturnsCustomerReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByCustomerId(1L)).thenReturn(reservations);

        List<ReservationDto> result = reservationService.getReservationsByCustomerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).customerId());
    }

    @Test
    void getReservationsBySpaceId_ReturnsSpaceReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findBySpaceId(1L)).thenReturn(reservations);

        List<ReservationDto> result = reservationService.getReservationsBySpaceId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).spaceId());
    }

    @Test
    void updateReservation_ValidReservation_ReturnsUpdatedReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.findOverlappingReservations(anyLong(), any(), any()))
                .thenReturn(Arrays.asList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationDto result = reservationService.updateReservation(1L, testReservationDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void updateReservation_NonExistentReservation_ThrowsResourceNotFoundException() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.updateReservation(999L, testReservationDto);
        });

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void deleteReservation_ExistingReservation_DeletesSuccessfully() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            reservationService.deleteReservation(1L);
        });

        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void deleteReservation_NonExistentReservation_ThrowsResourceNotFoundException() {
        when(reservationRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.deleteReservation(999L);
        });

        verify(reservationRepository, never()).deleteById(any());
    }

    @Test
    void confirmReservation_ValidReservation_ReturnsConfirmedReservation() {
        testReservation.setStatus(Status.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationDto result = reservationService.confirmReservation(1L);

        assertNotNull(result);
        assertEquals(Status.CONFIRMED, result.status());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_ValidReservation_ReturnsCancelledReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationDto result = reservationService.cancelReservation(1L);

        assertNotNull(result);
        assertEquals(Status.CANCELLED, result.status());
        verify(reservationRepository).save(any(Reservation.class));
    }
} 