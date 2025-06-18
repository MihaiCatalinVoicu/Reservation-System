package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.TenantDto;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public TenantDto createTenant(TenantDto tenantDto) {
        validateTenantData(tenantDto);
        
        if (tenantRepository.existsBySubdomain(tenantDto.getSubdomain())) {
            throw new ValidationException("Subdomain already exists: " + tenantDto.getSubdomain());
        }
        
        if (tenantRepository.existsByName(tenantDto.getName())) {
            throw new ValidationException("Tenant name already exists: " + tenantDto.getName());
        }

        Tenant tenant = TenantDto.Mapper.toEntity(tenantDto);
        tenant.setStatus(Tenant.TenantStatus.PENDING);
        tenant.setSubscriptionStartDate(LocalDateTime.now());
        
        // Set default values based on plan
        setDefaultLimitsByPlan(tenant);
        
        tenant = tenantRepository.save(tenant);
        return TenantDto.Mapper.toDto(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return TenantDto.Mapper.toDto(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantBySubdomain(String subdomain) {
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with subdomain: " + subdomain));
        return TenantDto.Mapper.toDto(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(TenantDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getTenantsByStatus(TenantDto.TenantStatus status) {
        return tenantRepository.findByStatus(Tenant.TenantStatus.valueOf(status.name())).stream()
                .map(TenantDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getTenantsByPlan(TenantDto.TenantPlan plan) {
        return tenantRepository.findByPlan(Tenant.TenantPlan.valueOf(plan.name())).stream()
                .map(TenantDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantDto updateTenant(Long id, TenantDto tenantDto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        // Check if subdomain is being changed and if it's already taken
        if (!tenant.getSubdomain().equals(tenantDto.getSubdomain()) && 
            tenantRepository.existsBySubdomain(tenantDto.getSubdomain())) {
            throw new ValidationException("Subdomain already exists: " + tenantDto.getSubdomain());
        }

        // Update fields
        tenant.setName(tenantDto.getName());
        tenant.setSubdomain(tenantDto.getSubdomain());
        tenant.setDisplayName(tenantDto.getDisplayName());
        tenant.setDescription(tenantDto.getDescription());
        tenant.setContactEmail(tenantDto.getContactEmail());
        tenant.setContactPhone(tenantDto.getContactPhone());
        tenant.setAddress(tenantDto.getAddress());
        tenant.setCity(tenantDto.getCity());
        tenant.setCountry(tenantDto.getCountry());
        tenant.setTimezone(tenantDto.getTimezone());
        tenant.setLogoUrl(tenantDto.getLogoUrl());
        tenant.setPrimaryColor(tenantDto.getPrimaryColor());
        tenant.setSecondaryColor(tenantDto.getSecondaryColor());
        tenant.setPlan(Tenant.TenantPlan.valueOf(tenantDto.getPlan().name()));
        tenant.setSubscriptionEndDate(tenantDto.getSubscriptionEndDate());
        
        // Update limits based on new plan
        setDefaultLimitsByPlan(tenant);

        tenant = tenantRepository.save(tenant);
        return TenantDto.Mapper.toDto(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        tenantRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void suspendTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void activateTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getExpiredSubscriptions() {
        return tenantRepository.findExpiredSubscriptions(LocalDateTime.now()).stream()
                .map(TenantDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getSubscriptionsExpiringBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return tenantRepository.findSubscriptionsExpiringBetween(startDate, endDate).stream()
                .map(TenantDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTenantActive(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .map(tenant -> tenant.getStatus() == Tenant.TenantStatus.ACTIVE)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTenantActive(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain)
                .map(tenant -> tenant.getStatus() == Tenant.TenantStatus.ACTIVE)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkTenantLimits(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        
        // Here you would implement logic to check if tenant has exceeded their limits
        // For now, we'll just check if they're active
        if (tenant.getStatus() != Tenant.TenantStatus.ACTIVE) {
            throw new ValidationException("Tenant is not active");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveTenantsCount() {
        return tenantRepository.countActiveTenants();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTenantsCountByPlan(TenantDto.TenantPlan plan) {
        return tenantRepository.countByPlan(Tenant.TenantPlan.valueOf(plan.name()));
    }

    private void validateTenantData(TenantDto tenantDto) {
        if (tenantDto.getName() == null || tenantDto.getName().trim().isEmpty()) {
            throw new ValidationException("Tenant name is required");
        }
        if (tenantDto.getSubdomain() == null || tenantDto.getSubdomain().trim().isEmpty()) {
            throw new ValidationException("Subdomain is required");
        }
        if (tenantDto.getContactEmail() == null || tenantDto.getContactEmail().trim().isEmpty()) {
            throw new ValidationException("Contact email is required");
        }
        if (tenantDto.getDisplayName() == null || tenantDto.getDisplayName().trim().isEmpty()) {
            throw new ValidationException("Display name is required");
        }
    }

    private void setDefaultLimitsByPlan(Tenant tenant) {
        switch (tenant.getPlan()) {
            case BASIC:
                tenant.setMaxUsers(10);
                tenant.setMaxSpaces(50);
                tenant.setMaxReservationsPerMonth(1000);
                break;
            case PREMIUM:
                tenant.setMaxUsers(50);
                tenant.setMaxSpaces(200);
                tenant.setMaxReservationsPerMonth(5000);
                break;
            case ENTERPRISE:
                tenant.setMaxUsers(200);
                tenant.setMaxSpaces(1000);
                tenant.setMaxReservationsPerMonth(25000);
                break;
        }
    }
} 