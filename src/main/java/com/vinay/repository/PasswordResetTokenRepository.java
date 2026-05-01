package com.vinay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinay.entity.PasswordResetToken;
import com.vinay.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	Optional<PasswordResetToken> findByToken(String token);
	Optional<PasswordResetToken> findByUser(User user);
}
