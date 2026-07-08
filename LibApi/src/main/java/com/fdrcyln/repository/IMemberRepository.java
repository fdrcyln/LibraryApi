package com.fdrcyln.repository;

import com.fdrcyln.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IMemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByActiveTrue();
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    java.util.List<Member> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);
    boolean existsByEmailIgnoreCaseAndIdNotAndActiveTrue(String email, Long id);
}