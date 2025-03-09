package com.pooja.zest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pooja.zest.entity.Employee;
import com.pooja.zest.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee addEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}

	public Optional<Employee> getEmpById(int id) {
		return employeeRepository.findById(id);
	}

	public Employee updateEmp(Employee employee) {
		return employeeRepository.save(employee);
	}

	public boolean deleteEmp(int id) {
		try {
			employeeRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Employee> getAllEmployees() {
		List<Employee> employees = employeeRepository.findAll();
		return employees;

	}
	
	public Page<Employee> getEmpWithPagination(int offset, int pageSize) {
		Pageable pageable = PageRequest.of(offset, pageSize);
		return employeeRepository.findAll(pageable);
	}

	public Page<Employee> getEmpWithPaginationAndSorting(int offset, int pageSize, String field) {
		Pageable pageable = PageRequest.of(offset, pageSize, Sort.by(field).ascending());
		return employeeRepository.findAll(pageable);
	}

}
