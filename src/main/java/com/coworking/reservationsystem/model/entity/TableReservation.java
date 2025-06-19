package com.coworking.reservationsystem.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_reservations")
@Getter
@Setter
public class TableReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull
    @JsonIgnore
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Customer customer;

    @NotNull
    @Min(value = 1, message = "Number of people must be at least 1")
    private Integer numberOfPeople;

    @NotNull
    @Column(name = "requested_time", nullable = false)
    private LocalDateTime requestedTime;

    @NotNull
    @Column(name = "estimated_arrival_time", nullable = false)
    private LocalDateTime estimatedArrivalTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TableReservationStatus status = TableReservationStatus.PENDING;

    @Column(name = "special_requests", length = 500)
    private String specialRequests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TableReservationStatus {
        PENDING,        // Cerere în așteptare
        CONFIRMED,      // Confirmată de ospătar
        REJECTED,       // Respinsă de ospătar
        CANCELLED,      // Anulată de client
        COMPLETED,      // Clientul a sosit și a fost servit
        EXPIRED         // Expirată (clientul nu a sosit)
    }
} 