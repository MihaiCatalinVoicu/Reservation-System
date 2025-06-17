package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.ReservationDto;
import com.coworking.reservationsystem.model.dto.Status;
import com.coworking.reservationsystem.model.entity.Reservation;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.ReservationRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;

    @Override
    @Transactional
    public ReservationDto createReservation(ReservationDto reservationDto) {
        validateReservationDates(reservationDto);
        checkForOverlappingReservations(reservationDto);

        User user = userRepository.findById(reservationDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Space space = spaceRepository.findById(reservationDto.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        Reservation reservation = ReservationDto.Mapper.toEntity(reservationDto);
        reservation.setUser(user);
        reservation.setSpace(space);
        reservation.setStatus(Status.PENDING);

        return ReservationDto.Mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(ReservationDto.Mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReservationDto.Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(ReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUserId(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(ReservationDto.Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsBySpaceId(Long spaceId) {
        return reservationRepository.findBySpaceId(spaceId).stream()
                .map(ReservationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsBySpaceId(Long spaceId, Pageable pageable) {
        return reservationRepository.findBySpaceId(spaceId, pageable)
                .map(ReservationDto.Mapper::toDto);
    }

    @Override
    @Transactional
    public ReservationDto updateReservation(Long id, ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        validateReservationDates(reservationDto);
        checkForOverlappingReservations(reservationDto);

        reservation.setStartTime(reservationDto.startTime());
        reservation.setEndTime(reservationDto.endTime());
        reservation.setTotalPrice(reservationDto.totalPrice());
        reservation.setStatus(reservationDto.status());

        return ReservationDto.Mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReservationDto confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() != Status.PENDING) {
            throw new ValidationException("Only pending reservations can be confirmed");
        }

        reservation.setStatus(Status.CONFIRMED);
        return ReservationDto.Mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationDto cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() == Status.CANCELLED) {
            throw new ValidationException("Reservation is already cancelled");
        }

        reservation.setStatus(Status.CANCELLED);
        return ReservationDto.Mapper.toDto(reservationRepository.save(reservation));
    }

    private void validateReservationDates(ReservationDto reservationDto) {
        LocalDateTime now = LocalDateTime.now();
        if (reservationDto.startTime().isBefore(now)) {
            throw new ValidationException("Start time cannot be in the past");
        }
        if (reservationDto.endTime().isBefore(reservationDto.startTime())) {
            throw new ValidationException("End time must be after start time");
        }
    }

    private void checkForOverlappingReservations(ReservationDto reservationDto) {
        List<Reservation> overlappingReservations = reservationRepository
                .findOverlappingReservations(
                        reservationDto.spaceId(),
                        reservationDto.startTime(),
                        reservationDto.endTime()
                );

        if (!overlappingReservations.isEmpty()) {
            throw new ValidationException("There are overlapping reservations for this space");
        }
    }
} 