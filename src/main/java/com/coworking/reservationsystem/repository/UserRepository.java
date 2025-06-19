package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);
   boolean existsByEmail(String email);
   List<User> findByTenantId(Long tenantId);
   Optional<User> findByIdAndTenantId(Long id, Long tenantId);
}
