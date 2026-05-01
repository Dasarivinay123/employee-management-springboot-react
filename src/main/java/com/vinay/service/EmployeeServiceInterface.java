package com.vinay.service;

import org.springframework.data.domain.Page;

import com.vinay.dto.EmployeeDto;


public interface EmployeeServiceInterface {

	EmployeeDto createEmployee(EmployeeDto employeeDto);
	EmployeeDto getEmployeeById(Long employeeId);
	Page<EmployeeDto> getAllEmployees(int page, int size, String keyword);
	EmployeeDto updateEmployee(Long employeeId, EmployeeDto employeeDto);
	void deleteEmployee(Long employeeId);
}
