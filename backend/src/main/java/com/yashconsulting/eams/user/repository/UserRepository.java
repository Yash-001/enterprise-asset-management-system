package com.yashconsulting.eams.user.repository;

import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByRole(Role role);
}
