package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.ReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(ReservationDto reservationDto);
    ReservationDto getReservationById(Long id);
    List<ReservationDto> getAllReservations();
    Page<ReservationDto> getAllReservations(Pageable pageable);
    List<ReservationDto> getReservationsByCustomerId(Long customerId);
    Page<ReservationDto> getReservationsByCustomerId(Long customerId, Pageable pageable);
    List<ReservationDto> getReservationsBySpaceId(Long spaceId);
    Page<ReservationDto> getReservationsBySpaceId(Long spaceId, Pageable pageable);
    ReservationDto updateReservation(Long id, ReservationDto reservationDto);
    void deleteReservation(Long id);
    ReservationDto confirmReservation(Long id);
    ReservationDto cancelReservation(Long id);
}
