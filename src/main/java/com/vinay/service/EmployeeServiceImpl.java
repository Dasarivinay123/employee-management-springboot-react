package com.vinay.service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.vinay.dto.EmployeeDto;
import com.vinay.entity.Employee;
import com.vinay.exception.DuplicateResourceException;
import com.vinay.exception.ResourceNotFoundException;
import com.vinay.mapper.EmployeeMapper;
import com.vinay.repository.EmployeeInterface;

@Service
public class EmployeeServiceImpl implements EmployeeServiceInterface {

	@Autowired
	private EmployeeInterface employeeInterfaceRepo;
	
	@Override
	public EmployeeDto createEmployee(EmployeeDto employeeDto) {

		Optional<Employee> email = employeeInterfaceRepo.findByEmail(employeeDto.getEmail());
		email.ifPresent(emp ->{
			throw new DuplicateResourceException( "Employee already exists with email: " + employeeDto.getEmail());
		});
		Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
		Employee savedEmployee = employeeInterfaceRepo.save(employee);
		EmployeeDto toEmployeeDto = EmployeeMapper.mapToEmployeeDto(savedEmployee);
		return toEmployeeDto;
	}

	@Override
	public EmployeeDto getEmployeeById(Long employeeId) {
		Employee employee = employeeInterfaceRepo.findById(employeeId)
			    .orElseThrow(() -> new ResourceNotFoundException(
			        String.format("Employee does not exist with given Id: %d", employeeId)
			    ));
		
		return EmployeeMapper.mapToEmployeeDto(employee);
	}

	@Override
	public Page<EmployeeDto> getAllEmployees(int page, int size, String keyword) {

	    Pageable pageable = PageRequest.of(page, size);

	    Page<Employee> employeePage;

	    if (keyword != null && !keyword.isEmpty()) {
	        employeePage = employeeInterfaceRepo
	            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
	                keyword, keyword, keyword, pageable
	            );
	    } else {
	        employeePage = employeeInterfaceRepo.findAll(pageable);
	    }

	    return employeePage.map(employee ->
	        EmployeeMapper.mapToEmployeeDto(employee)
	    );
	}

	@Override
	public EmployeeDto updateEmployee(Long employeeId, EmployeeDto employeeDto) {

	    Employee employee = employeeInterfaceRepo.findById(employeeId)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            String.format("Employee does not exist with given Id: %d", employeeId)
	        ));
	    employee.setFirstName(employeeDto.getFirstName());
	    employee.setLastName(employeeDto.getLastName());
	    employee.setEmail(employeeDto.getEmail());

	    Employee updatedEmployee = employeeInterfaceRepo.save(employee);
	    
	    return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
	}

	@Override
	public void deleteEmployee(Long employeeId) {

	    Employee employee = employeeInterfaceRepo.findById(employeeId)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            String.format("Employee does not exist with given Id: %d", employeeId)
	        ));
	    employeeInterfaceRepo.delete(employee);
	}
}
