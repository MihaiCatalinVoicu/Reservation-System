package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.TenantDto;
import com.coworking.reservationsystem.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody TenantDto tenantDto) {
        try {
            TenantDto createdTenant = tenantService.createTenant(tenantDto);
            return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDto> getTenantById(@PathVariable Long id) {
        try {
            TenantDto tenant = tenantService.getTenantById(id);
            return ResponseEntity.ok(tenant);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subdomain/{subdomain}")
    public ResponseEntity<TenantDto> getTenantBySubdomain(@PathVariable String subdomain) {
        try {
            TenantDto tenant = tenantService.getTenantBySubdomain(subdomain);
            return ResponseEntity.ok(tenant);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TenantDto>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TenantDto>> getTenantsByStatus(@PathVariable TenantDto.TenantStatus status) {
        return ResponseEntity.ok(tenantService.getTenantsByStatus(status));
    }

    @GetMapping("/plan/{plan}")
    public ResponseEntity<List<TenantDto>> getTenantsByPlan(@PathVariable TenantDto.TenantPlan plan) {
        return ResponseEntity.ok(tenantService.getTenantsByPlan(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDto> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantDto tenantDto) {
        try {
            TenantDto updatedTenant = tenantService.updateTenant(id, tenantDto);
            return ResponseEntity.ok(updatedTenant);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        try {
            tenantService.deleteTenant(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendTenant(@PathVariable Long id) {
        try {
            tenantService.suspendTenant(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateTenant(@PathVariable Long id) {
        try {
            tenantService.activateTenant(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<List<TenantDto>> getExpiredSubscriptions() {
        List<TenantDto> expiredTenants = tenantService.getExpiredSubscriptions();
        return ResponseEntity.ok(expiredTenants);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<TenantDto>> getSubscriptionsExpiringBetween(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<TenantDto> expiringTenants = tenantService.getSubscriptionsExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(expiringTenants);
    }

    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isTenantActive(@PathVariable Long id) {
        try {
            boolean isActive = tenantService.isTenantActive(id);
            return ResponseEntity.ok(isActive);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subdomain/{subdomain}/active")
    public ResponseEntity<Boolean> isTenantActiveBySubdomain(@PathVariable String subdomain) {
        try {
            boolean isActive = tenantService.isTenantActive(subdomain);
            return ResponseEntity.ok(isActive);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveTenantsCount() {
        long count = tenantService.getActiveTenantsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/plan-count/{plan}")
    public ResponseEntity<Long> getTenantsCountByPlan(@PathVariable TenantDto.TenantPlan plan) {
        long count = tenantService.getTenantsCountByPlan(plan);
        return ResponseEntity.ok(count);
    }
} 