package com.example.rqchallenge.service;

import com.example.rqchallenge.employees.client.EmployeeClient;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotExist;
import com.example.rqchallenge.employees.exceptions.InvalidIdException;
import com.example.rqchallenge.employees.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ValidationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Aarav Patel", result.get(0).getEmployeeName());
        assertEquals("Aditya Mehta", result.get(1).getEmployeeName());
    }

    @Test
    public void testGetAllEmployeesWhenException() {
        when(employeeClient.fetchAllEmployees()).thenThrow(new RuntimeException("Fetch error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals("Error occurred while fetching employees", exception.getMessage());
    }

    @Test
    public void testSearchEmployeesByName() {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        List<Employee> result = employeeService.searchEmployeesByName("Aarav");

        assertEquals(1, result.size());
        assertEquals("Aarav Patel", result.get(0).getEmployeeName());
    }

    @Test
    public void testSearchEmployeesByNameWhenException() {
        when(employeeClient.fetchAllEmployees()).thenThrow(new RuntimeException("Fetch error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.searchEmployeesByName("Aditya");
        });

        assertEquals("Error occurred while fetching employees by name Aditya", exception.getMessage());
    }

    @Test
    public void testGetEmployeeById() {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        Employee result = employeeService.getEmployeeById("1");

        assertEquals("Aarav Patel", result.getEmployeeName());
    }

    @Test
    public void testGetEmployeeByIdWhenInvalidId() {
        Exception exception = assertThrows(InvalidIdException.class, () -> {
            employeeService.getEmployeeById("abc");
        });

        assertEquals("Invalid ID format. ID should be a number.", exception.getMessage());
    }

    @Test
    public void testGetEmployeeByIdWhenEmployeeNotExist() {
        List<Employee> employees = Collections.emptyList();

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        Exception exception = assertThrows(EmployeeNotExist.class, () -> {
            employeeService.getEmployeeById("1");
        });

        assertEquals("Employee with ID 1 does not exist", exception.getMessage());
    }

    @Test
    public void testGetHighestSalaryOfEmployee() {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        Integer result = employeeService.getHighestSalaryOfEmployee();

        assertEquals(60000, result);
    }

    @Test
    public void testGetHighestSalaryOfEmployeeWhenException() {
        when(employeeClient.fetchAllEmployees()).thenThrow(new RuntimeException("Fetch error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getHighestSalaryOfEmployee();
        });

        assertEquals("Error occurred while fetching highest salary", exception.getMessage());
    }

    @Test
    public void testGetHighestSalaryOfEmployeeForEmptyList() {
        List<Employee> employees = Collections.emptyList();

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        Exception exception = assertThrows(EmployeeNotExist.class, () -> {
            employeeService.getHighestSalaryOfEmployee();
        });

        assertEquals("No employees found in the list", exception.getMessage());
    }






    @Test
    public void testGetTopTenHighestSalaryEmployeeNames() {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, ""),
                new Employee("3", "Isha Sharma", 65000, 25, "")
        );

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        List<String> result = employeeService.getTopTenHighestSalaryEmployeeNames();

        assertEquals(3, result.size());
        assertTrue(result.contains("Isha Sharma"));
        assertTrue(result.contains("Aditya Mehta"));
    }

    @Test
    public void testGetTopTenHighestSalaryEmployeeNamesForEmptyList() {
        List<Employee> employees = Collections.emptyList();

        when(employeeClient.fetchAllEmployees()).thenReturn(employees);

        Exception exception = assertThrows(EmployeeNotExist.class, () -> {
            employeeService.getTopTenHighestSalaryEmployeeNames();
        });

        assertEquals("No employees found in the list", exception.getMessage());
    }

    @Test
    public void testGetTopTenHighestSalaryEmployeeNamesWhenException() {
        when(employeeClient.fetchAllEmployees()).thenThrow(new RuntimeException("Fetch error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getTopTenHighestSalaryEmployeeNames();
        });

        assertEquals("Error occurred while fetching all employees with top ten salary", exception.getMessage());
    }

    @Test
    public void testCreateEmployee() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee_name", "Sunil kadam");
        input.put("employee_salary", 40000);
        input.put("employee_age", 30);
        input.put("profile_image", "");

        Employee employee = new Employee("101", "Sunil kadam", 40000, 30, "");

        when(employeeClient.createEmployee(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.createEmployee(input);

        assertEquals("Sunil kadam", result.getEmployeeName());
    }


    @Test
    public void testCreateEmployeeWhenValidationException() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee_name", "Sunil kadam");
        input.put("employee_salary", "abc");
        input.put("employee_age", 30);
        input.put("profile_image", "");

        Exception exception = assertThrows(ValidationException.class, () -> {
            employeeService.createEmployee(input);
        });

        assertEquals("Error converting map to Employee: Invalid data types", exception.getMessage());
    }


    @Test
    public void testCreateEmployeeWhenException() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee_name", "Sunil kadam");
        input.put("employee_salary", 40000);
        input.put("employee_age", 30);
        input.put("profile_image", "");

        when(employeeClient.createEmployee(any(Employee.class))).thenThrow(new RuntimeException("Create error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(input);
        });

        assertEquals("Create error", exception.getMessage());
    }

    @Test
    public void testDeleteEmployee() {
        doNothing().when(employeeClient).deleteEmployeeById("103");

        employeeService.deleteEmployee("103");

        verify(employeeClient, times(1)).deleteEmployeeById("103");
    }

    @Test
    public void testDeleteEmployeeWhenException() {
        doThrow(new RuntimeException("Delete error")).when(employeeClient).deleteEmployeeById("103");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.deleteEmployee("103");
        });

        assertEquals("Error occurred while deleting employee by id 103", exception.getMessage());
    }
}
