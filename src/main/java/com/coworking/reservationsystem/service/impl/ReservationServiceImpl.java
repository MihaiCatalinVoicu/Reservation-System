package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.ReservationDto;
import com.coworking.reservationsystem.model.entity.Reservation;
import com.coworking.reservationsystem.repository.ReservationRepository;
import com.coworking.reservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public ReservationDto createReservation(ReservationDto reservationDto) {
        Reservation reservation = ReservationDto.Mapper.toEntity(reservationDto);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationDto.Mapper.toDto(savedReservation);
    }

    @Override
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return ReservationDto.Mapper.toDto(reservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDto updateReservation(Long id, ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStartTime(reservationDto.startTime());
        reservation.setEndTime(reservationDto.endTime());
        reservation.setTotalPrice(reservationDto.totalPrice());
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDto.Mapper.toDto(updatedReservation);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }
} 