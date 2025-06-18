package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Subdomain is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
    private String subdomain;
    
    private String displayName;
    private String description;
    
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;
    
    @Pattern(regexp = "^[+]?[0-9\\s-()]+$", message = "Contact phone must be a valid phone number")
    private String contactPhone;
    
    private String address;
    private String city;
    private String country;
    private String timezone;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    
    @NotNull(message = "Status is required")
    private TenantStatus status;
    
    @NotNull(message = "Plan is required")
    private TenantPlan plan;
    
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    
    @Positive(message = "Max users must be positive")
    private Integer maxUsers;
    
    @Positive(message = "Max spaces must be positive")
    private Integer maxSpaces;
    
    @Positive(message = "Max reservations per month must be positive")
    private Integer maxReservationsPerMonth;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TenantStatus {
        ACTIVE, SUSPENDED, CANCELLED, PENDING
    }

    public enum TenantPlan {
        BASIC, PREMIUM, ENTERPRISE
    }

    public static class Mapper {
        public static TenantDto toDto(Tenant tenant) {
            return new TenantDto(
                    tenant.getId(),
                    tenant.getName(),
                    tenant.getSubdomain(),
                    tenant.getDisplayName(),
                    tenant.getDescription(),
                    tenant.getContactEmail(),
                    tenant.getContactPhone(),
                    tenant.getAddress(),
                    tenant.getCity(),
                    tenant.getCountry(),
                    tenant.getTimezone(),
                    tenant.getLogoUrl(),
                    tenant.getPrimaryColor(),
                    tenant.getSecondaryColor(),
                    TenantStatus.valueOf(tenant.getStatus().name()),
                    TenantPlan.valueOf(tenant.getPlan().name()),
                    tenant.getSubscriptionStartDate(),
                    tenant.getSubscriptionEndDate(),
                    tenant.getMaxUsers(),
                    tenant.getMaxSpaces(),
                    tenant.getMaxReservationsPerMonth(),
                    tenant.getCreatedAt(),
                    tenant.getUpdatedAt()
            );
        }

        public static Tenant toEntity(TenantDto dto) {
            Tenant tenant = new Tenant();
            tenant.setId(dto.getId());
            tenant.setName(dto.getName());
            tenant.setSubdomain(dto.getSubdomain());
            tenant.setDisplayName(dto.getDisplayName());
            tenant.setDescription(dto.getDescription());
            tenant.setContactEmail(dto.getContactEmail());
            tenant.setContactPhone(dto.getContactPhone());
            tenant.setAddress(dto.getAddress());
            tenant.setCity(dto.getCity());
            tenant.setCountry(dto.getCountry());
            tenant.setTimezone(dto.getTimezone());
            tenant.setLogoUrl(dto.getLogoUrl());
            tenant.setPrimaryColor(dto.getPrimaryColor());
            tenant.setSecondaryColor(dto.getSecondaryColor());
            tenant.setStatus(Tenant.TenantStatus.valueOf(dto.getStatus().name()));
            tenant.setPlan(Tenant.TenantPlan.valueOf(dto.getPlan().name()));
            tenant.setSubscriptionStartDate(dto.getSubscriptionStartDate());
            tenant.setSubscriptionEndDate(dto.getSubscriptionEndDate());
            tenant.setMaxUsers(dto.getMaxUsers());
            tenant.setMaxSpaces(dto.getMaxSpaces());
            tenant.setMaxReservationsPerMonth(dto.getMaxReservationsPerMonth());
            return tenant;
        }
    }
} 