package com.example.rqchallenge.controller;

import com.example.rqchallenge.employees.controller.EmployeeController;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        List<Employee> employees = Arrays.asList(
                new Employee("1", "Aarav Patel", 50000, 30, ""),
                new Employee("2", "Aditya Mehta", 60000, 25, "")
        );

        when(employeeService.searchEmployeesByName(anyString())).thenReturn(employees);

        mockMvc.perform(get("/search/Mehta"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        Employee employee = new Employee("1", "Aarav Patel", 50000, 30, "");

        when(employeeService.getEmployeeById(anyString())).thenReturn(employee);

        mockMvc.perform(get("/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        when(employeeService.getHighestSalaryOfEmployee()).thenReturn(60000);

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> names = Arrays.asList("Aarav Patel", "Aditya Mehta");

        when(employeeService.getTopTenHighestSalaryEmployeeNames()).thenReturn(names);

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("employee_name", "Aarav Patel");
        employeeInput.put("employee_salary", 50000);
        employeeInput.put("employee_age", 30);
        employeeInput.put("profile_image", "");

        Employee employee = new Employee("1", "John Doe", 50000, 30, "");

        when(employeeService.createEmployee(any(Map.class))).thenReturn(employee);

        mockMvc.perform(post("/")
                        .contentType("application/json")
                        .content("{\"employee_name\":\"John Doe\", \"employee_salary\":50000, \"employee_age\":30, \"profile_image\":\"\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        mockMvc.perform(delete("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully! deleted Record"));
    }
}
