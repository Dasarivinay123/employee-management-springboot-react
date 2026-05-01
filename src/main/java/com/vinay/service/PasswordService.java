package com.vinay.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vinay.entity.PasswordResetToken;
import com.vinay.entity.User;
import com.vinay.repository.PasswordResetTokenRepository;
import com.vinay.repository.UserRepository;

@Service
public class PasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordResetTokenRepository tokenRepo;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 🔹 1. Send reset email
	public void sendResetLink(String email) {

		if (email == null || email.isEmpty()) {
			throw new RuntimeException("Email cannot be empty");
		}

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		//  Delete old token if exists (IMPORTANT)
		tokenRepo.findByUser(user).ifPresent(tokenRepo::delete);

		String token = UUID.randomUUID().toString();

		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(token);
		resetToken.setUser(user);
		resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

		tokenRepo.save(resetToken);

		String resetLink = "http://localhost:3000/reset-password?token=" + token;

		emailService.sendResetPasswordEmail(user.getEmail(), user.getName(), resetLink); // OR create new method
		// Better:
		// emailService.sendResetPasswordEmail(email, resetLink);
	}

	//  2. Reset password
	public void resetPassword(String token, String newPassword) {
		System.out.println("RAW PASSWORD: " + newPassword);
		if (newPassword == null || newPassword.isEmpty()) {
			throw new RuntimeException("Password cannot be empty");
		}

		PasswordResetToken resetToken = tokenRepo.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid token"));

		if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			tokenRepo.delete(resetToken); // cleanup expired
			throw new RuntimeException("Token expired");
		}

		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword.trim()));

		userRepository.save(user);

		//  Always delete token after use
		tokenRepo.delete(resetToken);
	}
}