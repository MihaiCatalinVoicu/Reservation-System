package com.coworking.reservationsystem.service.impl;

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
import com.coworking.reservationsystem.service.TableReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableReservationServiceImpl implements TableReservationService {

    private final TableReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public TableReservationDto createTableReservation(TableReservationDto reservationDto) {
        // Validate table exists
        RestaurantTable table = tableRepository.findByIdAndTenantId(reservationDto.tableId(), reservationDto.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + reservationDto.tableId()));

        // Validate customer exists
        Customer customer = customerRepository.findById(reservationDto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + reservationDto.customerId()));

        // Validate tenant exists
        Tenant tenant = tenantRepository.findById(reservationDto.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + reservationDto.tenantId()));

        // Validate dates
        validateReservationDates(reservationDto);

        // Check for overlapping reservations
        if (hasOverlappingReservations(reservationDto.tableId(), reservationDto.requestedTime(), reservationDto.estimatedArrivalTime())) {
            throw new ValidationException("There are overlapping reservations for this table");
        }

        TableReservation reservation = TableReservationDto.Mapper.toEntity(reservationDto);
        reservation.setTable(table);
        reservation.setCustomer(customer);
        reservation.setTenant(tenant);
        reservation.setStatus(TableReservation.TableReservationStatus.PENDING);

        TableReservation savedReservation = reservationRepository.save(reservation);
        return TableReservationDto.Mapper.toDto(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableReservationDto> getTableReservationById(Long id, Long tenantId) {
        return reservationRepository.findByIdAndTenantId(id, tenantId)
                .map(TableReservationDto.Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getAllTableReservationsByTenant(Long tenantId) {
        return reservationRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getTableReservationsByCustomer(Long customerId, Long tenantId) {
        return reservationRepository.findByCustomerIdAndTenantIdOrderByCreatedAtDesc(customerId, tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getTableReservationsByTable(Long tableId, Long tenantId) {
        return reservationRepository.findByTableIdAndTenantIdOrderByCreatedAtDesc(tableId, tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getTableReservationsByStatus(TableReservation.TableReservationStatus status, Long tenantId) {
        return reservationRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getPendingTableReservations(Long tenantId) {
        return reservationRepository.findByStatusAndTenantIdOrderByRequestedTimeAsc(TableReservation.TableReservationStatus.PENDING, tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<TableReservationDto> updateTableReservation(Long id, TableReservationDto reservationDto) {
        return reservationRepository.findByIdAndTenantId(id, reservationDto.tenantId())
                .map(reservation -> {
                    validateReservationDates(reservationDto);
                    
                    if (hasOverlappingReservations(reservationDto.tableId(), reservationDto.requestedTime(), reservationDto.estimatedArrivalTime())) {
                        throw new ValidationException("There are overlapping reservations for this table");
                    }

                    reservation.setNumberOfPeople(reservationDto.numberOfPeople());
                    reservation.setRequestedTime(reservationDto.requestedTime());
                    reservation.setEstimatedArrivalTime(reservationDto.estimatedArrivalTime());
                    reservation.setStatus(reservationDto.status());
                    reservation.setSpecialRequests(reservationDto.specialRequests());

                    TableReservation updatedReservation = reservationRepository.save(reservation);
                    return TableReservationDto.Mapper.toDto(updatedReservation);
                });
    }

    @Override
    @Transactional
    public Optional<TableReservationDto> confirmTableReservation(Long id, Long tenantId) {
        return reservationRepository.findByIdAndTenantId(id, tenantId)
                .map(reservation -> {
                    if (reservation.getStatus() != TableReservation.TableReservationStatus.PENDING) {
                        throw new ValidationException("Only pending reservations can be confirmed");
                    }
                    reservation.setStatus(TableReservation.TableReservationStatus.CONFIRMED);
                    TableReservation updatedReservation = reservationRepository.save(reservation);
                    return TableReservationDto.Mapper.toDto(updatedReservation);
                });
    }

    @Override
    @Transactional
    public Optional<TableReservationDto> rejectTableReservation(Long id, Long tenantId) {
        return reservationRepository.findByIdAndTenantId(id, tenantId)
                .map(reservation -> {
                    if (reservation.getStatus() != TableReservation.TableReservationStatus.PENDING) {
                        throw new ValidationException("Only pending reservations can be rejected");
                    }
                    reservation.setStatus(TableReservation.TableReservationStatus.REJECTED);
                    TableReservation updatedReservation = reservationRepository.save(reservation);
                    return TableReservationDto.Mapper.toDto(updatedReservation);
                });
    }

    @Override
    @Transactional
    public Optional<TableReservationDto> cancelTableReservation(Long id, Long tenantId) {
        return reservationRepository.findByIdAndTenantId(id, tenantId)
                .map(reservation -> {
                    if (reservation.getStatus() == TableReservation.TableReservationStatus.CANCELLED) {
                        throw new ValidationException("Reservation is already cancelled");
                    }
                    reservation.setStatus(TableReservation.TableReservationStatus.CANCELLED);
                    TableReservation updatedReservation = reservationRepository.save(reservation);
                    return TableReservationDto.Mapper.toDto(updatedReservation);
                });
    }

    @Override
    @Transactional
    public Optional<TableReservationDto> completeTableReservation(Long id, Long tenantId) {
        return reservationRepository.findByIdAndTenantId(id, tenantId)
                .map(reservation -> {
                    if (reservation.getStatus() != TableReservation.TableReservationStatus.CONFIRMED) {
                        throw new ValidationException("Only confirmed reservations can be completed");
                    }
                    reservation.setStatus(TableReservation.TableReservationStatus.COMPLETED);
                    TableReservation updatedReservation = reservationRepository.save(reservation);
                    return TableReservationDto.Mapper.toDto(updatedReservation);
                });
    }

    @Override
    @Transactional
    public boolean deleteTableReservation(Long id, Long tenantId) {
        Optional<TableReservation> reservation = reservationRepository.findByIdAndTenantId(id, tenantId);
        if (reservation.isPresent()) {
            reservationRepository.delete(reservation.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableReservationDto> getTableReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long tenantId) {
        return reservationRepository.findByDateRangeAndTenantId(startDate, endDate, tenantId).stream()
                .map(TableReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TableReservationDto> getTableReservationsByCustomer(Long customerId, Long tenantId, Pageable pageable) {
        return reservationRepository.findByCustomerIdAndTenantId(customerId, tenantId, pageable)
                .map(TableReservationDto.Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOverlappingReservations(Long tableId, LocalDateTime startTime, LocalDateTime endTime) {
        List<TableReservation> overlappingReservations = reservationRepository.findOverlappingReservations(tableId, startTime, endTime);
        return !overlappingReservations.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTableReservationCountByStatus(TableReservation.TableReservationStatus status, Long tenantId) {
        return reservationRepository.countByStatusAndTenantId(status, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTableReservationCountByTenant(Long tenantId) {
        return reservationRepository.countByTenantId(tenantId);
    }

    private void validateReservationDates(TableReservationDto reservationDto) {
        LocalDateTime now = LocalDateTime.now();
        if (reservationDto.requestedTime().isBefore(now)) {
            throw new ValidationException("Requested time cannot be in the past");
        }
        if (reservationDto.estimatedArrivalTime().isBefore(reservationDto.requestedTime())) {
            throw new ValidationException("Estimated arrival time must be after requested time");
        }
    }
} 