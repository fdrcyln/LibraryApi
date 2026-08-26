package com.fdrcyln.repository;

import com.fdrcyln.entities.UserAccount;
import com.fdrcyln.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);
}
