package com.pooja.zest.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooja.zest.entity.Employee;
import com.pooja.zest.service.EmployeeService;

@RestController
@RequestMapping(path = "/emp")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping
	protected ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
		Employee emp = employeeService.addEmployee(employee);
		return new ResponseEntity<>(emp, HttpStatus.CREATED);
	}

	@GetMapping(path = "/{id}")
	protected ResponseEntity<Object> getEmpById(@PathVariable int id) {
		Optional<Employee> user = employeeService.getEmpById(id);
		if (Objects.nonNull(user))
			return new ResponseEntity<Object>(user, HttpStatus.FOUND);
		else
			return new ResponseEntity<Object>("Invalid id, User not found", HttpStatus.NOT_FOUND);
	}

	@PutMapping
	protected Employee updateEmp(@RequestBody Employee employee) {
		return employeeService.updateEmp(employee);
	}

	@DeleteMapping(path = "/{id}")
	protected ResponseEntity<String> deleteEmp(@PathVariable int id) {
		boolean deleteEmp = employeeService.deleteEmp(id);
		
			if (deleteEmp)
				return new ResponseEntity<String>("Employee deleted", HttpStatus.OK);
			else
				return new ResponseEntity<String>("Employee not deleted, Something went wrong", HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/admin/all")
	protected List<Employee> getAllUsers() {
		List<Employee> employees = employeeService.getAllEmployees();
		return employees;
	}

	@GetMapping(path = "/admin/pagination/{offset}/{pageSize}")
	protected Page<Employee> getEmpWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
		return employeeService.getEmpWithPagination(offset, pageSize);
	}

	@GetMapping(path = "/admin/pagination/{offset}/{pageSize}/{field}")
	protected Page<Employee> getEmpWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize,
			@PathVariable String field) {
		return employeeService.getEmpWithPaginationAndSorting(offset, pageSize, field);
	}

}
