package com.vinay.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vinay.dto.LogInDTO;
import com.vinay.dto.UserDTO;
import com.vinay.entity.User;
import com.vinay.exception.DuplicateResourceException;
import com.vinay.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public UserDTO createUser(UserDTO usersDto) {

    	 Optional<User> existingUser = userRepository.findByEmail(usersDto.getEmail());
    	 if (existingUser.isPresent()) {
    	        throw new DuplicateResourceException("Email already registered");
    	    }
        User user = usersDtoToEntity(usersDto);
        user.setPassword(passwordEncoder.encode(usersDto.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Send email (non-blocking safe way)
        try {
            emailService.sendRegistrationEmail(
                    savedUser.getEmail(),
                    savedUser.getName()
            );
        } catch (Exception e) {
            //Don't break registration if email fails
            System.out.println("Email sending failed: " + e.getMessage());
        }

        
        return entityTousersDto(savedUser);
    }

    private User usersDtoToEntity(UserDTO usersDto) {
        User user = new User();
        user.setName(usersDto.getName());
        user.setEmail(usersDto.getEmail());
        user.setPassword(usersDto.getPassword());
        user.setVerified(false);
        user.setRole("USER");
        return user;
    }

    private UserDTO entityTousersDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
       // dto.setPassword(user.getPassword());//do not expose password
        return dto;
    }

    @Override
    public String verify(LogInDTO logInDTO) {

        User user = userRepository.findByEmail(logInDTO.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        // Email verification check
        if (!user.isVerified()) {
            throw new RuntimeException(
                "Your account is not verified. Please check your email and click Verify/Login button."
            );
        }

        try {

            Authentication authenticate =
                    authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                            logInDTO.getEmail(),
                            logInDTO.getPassword()
                        )
                    );

            if (authenticate.isAuthenticated()) {

                // IMPORTANT CHANGE
                return jwtService.generateToken(user);
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }

        return "Failure";
    }
	@Override
	public void verifyAccount(String email) {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		user.setVerified(true);

		userRepository.save(user);
	}
	@Override
	public User getUserByEmail(String email) {

	    return userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                new RuntimeException("User not found"));
	}
    
}