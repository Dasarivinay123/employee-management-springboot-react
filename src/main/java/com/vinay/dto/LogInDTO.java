package com.vinay.dto;

public class LogInDTO {

	private String email;
	private String password;

	public LogInDTO() {

	}

	public LogInDTO(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LogInDTO [email=" + email + ", password=" + password + "]";
	}

}
