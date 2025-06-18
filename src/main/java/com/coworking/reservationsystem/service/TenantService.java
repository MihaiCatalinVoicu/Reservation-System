package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.TenantDto;
import com.coworking.reservationsystem.model.entity.Tenant;

import java.time.LocalDateTime;
import java.util.List;

public interface TenantService {
    
    TenantDto createTenant(TenantDto tenantDto);
    
    TenantDto getTenantById(Long id);
    
    TenantDto getTenantBySubdomain(String subdomain);
    
    List<TenantDto> getAllTenants();
    
    List<TenantDto> getTenantsByStatus(TenantDto.TenantStatus status);
    
    List<TenantDto> getTenantsByPlan(TenantDto.TenantPlan plan);
    
    TenantDto updateTenant(Long id, TenantDto tenantDto);
    
    void deleteTenant(Long id);
    
    void suspendTenant(Long id);
    
    void activateTenant(Long id);
    
    List<TenantDto> getExpiredSubscriptions();
    
    List<TenantDto> getSubscriptionsExpiringBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    boolean isTenantActive(Long tenantId);
    
    boolean isTenantActive(String subdomain);
    
    void checkTenantLimits(Long tenantId);
    
    long getActiveTenantsCount();
    
    long getTenantsCountByPlan(TenantDto.TenantPlan plan);
} 