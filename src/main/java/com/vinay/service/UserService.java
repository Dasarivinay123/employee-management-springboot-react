package com.vinay.service;

import com.vinay.dto.LogInDTO;
import com.vinay.dto.UserDTO;
import com.vinay.entity.User;

public interface UserService {

	UserDTO createUser(UserDTO userDto);

	String verify(LogInDTO logInDTO);

	void verifyAccount(String email);
	
	User getUserByEmail(String email);

}
