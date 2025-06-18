package com.coworking.reservationsystem.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String subdomain;
    
    @Column(nullable = false)
    private String displayName;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String contactEmail;
    
    @Column
    private String contactPhone;
    
    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String country;
    
    @Column
    private String timezone;
    
    @Column
    private String logoUrl;
    
    @Column
    private String primaryColor;
    
    @Column
    private String secondaryColor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantPlan plan = TenantPlan.BASIC;
    
    @Column(nullable = false)
    private LocalDateTime subscriptionStartDate;
    
    @Column
    private LocalDateTime subscriptionEndDate;
    
    @Column(nullable = false)
    private Integer maxUsers = 10;
    
    @Column(nullable = false)
    private Integer maxSpaces = 50;
    
    @Column(nullable = false)
    private Integer maxReservationsPerMonth = 1000;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations;
    
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
    
    public enum TenantStatus {
        ACTIVE, SUSPENDED, CANCELLED, PENDING
    }
    
    public enum TenantPlan {
        BASIC, PREMIUM, ENTERPRISE
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (subscriptionStartDate == null) {
            subscriptionStartDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 