package com.example.rqchallenge.employees.controller;


import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class EmployeeController implements IEmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        List<Employee> employeeList = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeeList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeeList = employeeService.searchEmployeesByName(searchString);
        return new ResponseEntity<>(employeeList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer employeeSalary = employeeService.getHighestSalaryOfEmployee();
        return new ResponseEntity<>(employeeSalary, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> employeeNames = employeeService.getTopTenHighestSalaryEmployeeNames();
        return new ResponseEntity<>(employeeNames, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {

        Employee employee = employeeService.createEmployee(employeeInput);
        return new ResponseEntity<>(employee, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("Successfully! deleted Record", HttpStatus.OK);
    }
}
