package com.vinay.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinay.dto.EmployeeDto;
import com.vinay.payload.ApiResponse;
import com.vinay.service.EmployeeServiceInterface;

import jakarta.validation.Valid;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeServiceInterface employeeService;

	@PostMapping
	public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {

		EmployeeDto saved = employeeService.createEmployee(employeeDto);

		ApiResponse<EmployeeDto> response = new ApiResponse<>(true, 201, "Employee created successfully", saved);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(
	        @PathVariable Long id) {

	    EmployeeDto employee = employeeService.getEmployeeById(id);

	    return ResponseEntity.ok(
	        new ApiResponse<>(true, 200,
	            "Employee fetched successfully", employee)
	    );
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getAllEmployees(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        @RequestParam(required = false) String keyword) {

		 Page<EmployeeDto> pageData =
			        employeeService.getAllEmployees(page,size,keyword);

			    return ResponseEntity.ok(
			        new ApiResponse<>(true,200,
			            "Employees fetched successfully", pageData)
			    );
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEmployee(
	        @PathVariable("id") Long employeeId,
	        @RequestBody EmployeeDto employeeDto) {

	    Authentication auth =
	        SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities()
	        .stream()
	        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    if (!isAdmin) {

	        String loggedUserEmail = auth.getName();

	        EmployeeDto existing =
	            employeeService.getEmployeeById(employeeId);

	        if (!existing.getEmail().equals(loggedUserEmail)) {

	            Map<String, String> error = new HashMap<>();
	            error.put("message",
	                "Access Denied. You can update only your own record.");

	            return ResponseEntity
	                .status(HttpStatus.FORBIDDEN)
	                .body(error);
	        }
	    }

	    EmployeeDto updated =
	        employeeService.updateEmployee(employeeId, employeeDto);

	    return ResponseEntity.ok(updated);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteEmployee(
	        @PathVariable Long id) {

	    employeeService.deleteEmployee(id);

	    return ResponseEntity.ok(
	        new ApiResponse<>(true,200,
	            "Employee deleted successfully", null)
	    );
	}
	
}
