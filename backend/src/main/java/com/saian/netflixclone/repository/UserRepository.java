package com.saian.netflixclone.repository;

import com.saian.netflixclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByEmail(String email);
}
