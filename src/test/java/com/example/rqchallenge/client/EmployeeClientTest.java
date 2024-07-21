package com.example.rqchallenge.client;

import com.example.rqchallenge.employees.client.EmployeeClient;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.dto.EmployeeCollectionDto;
import com.example.rqchallenge.employees.dto.GetEmployeeDto;
import com.example.rqchallenge.employees.helper.EmployeeFileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EmployeeFileUtils employeeFileUtils;

    @InjectMocks
    private EmployeeClient employeeClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchAllEmployeesSuccess() throws IOException {
        EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();
        employeeCollectionDto.setStatus("success");
        employeeCollectionDto.setData(Arrays.asList(new Employee("1", "Aarav Patel", 50000, 30, "")));

        when(restTemplate.getForObject(anyString(), eq(EmployeeCollectionDto.class))).thenReturn(employeeCollectionDto);

        List<Employee> employees = employeeClient.fetchAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeFileUtils, times(1)).saveEmployeeResponseToFile(employeeCollectionDto);
    }

    @Test
    public void testFetchAllEmployeesFallbackToFile() {
        when(restTemplate.getForObject(anyString(), eq(EmployeeCollectionDto.class))).thenThrow(new RuntimeException("Fetch Error"));
        EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();
        employeeCollectionDto.setStatus("success");
        employeeCollectionDto.setData(Arrays.asList(new Employee("1", "Aarav Patel", 50000, 30, "")));

        when(employeeFileUtils.fetchEmployeeFromFile()).thenReturn(employeeCollectionDto);

        List<Employee> employees = employeeClient.fetchAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeFileUtils, times(1)).fetchEmployeeFromFile();
    }

    @Test
    public void testFetchAllEmployeesFallbackToFileWhenNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(EmployeeCollectionDto.class))).thenReturn(null);;
        EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();
        employeeCollectionDto.setStatus("success");
        employeeCollectionDto.setData(Arrays.asList(new Employee("1", "Aarav Patel", 50000, 30, "")));

        when(employeeFileUtils.fetchEmployeeFromFile()).thenReturn(employeeCollectionDto);

        List<Employee> employees = employeeClient.fetchAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeFileUtils, times(1)).fetchEmployeeFromFile();
    }

    @Test
    public void testCreateEmployeeSuccess() {
        Employee employee = new Employee("102", "Aarav Patel", 50000, 30, "");
        GetEmployeeDto getEmployeeDto = new GetEmployeeDto();
        getEmployeeDto.setData(employee);

        when(restTemplate.postForObject(anyString(), any(Employee.class), eq(GetEmployeeDto.class))).thenReturn(getEmployeeDto);

        Employee createdEmployee = employeeClient.createEmployee(employee);

        assertNotNull(createdEmployee);
        assertEquals("Aarav Patel", createdEmployee.getEmployeeName());
    }

    @Test
    public void testCreateEmployeeRetryOnTooManyRequests() {
        Employee employee = new Employee("102", "Aarav Patel", 50000, 30, "");
        RestClientResponseException exception = mock(RestClientResponseException.class);

        when(exception.getRawStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS.value());
        when(exception.getStatusText()).thenReturn("Too many requests");

        when(restTemplate.postForObject(anyString(), any(Employee.class), eq(GetEmployeeDto.class))).thenThrow(exception);

        Exception thrownException = assertThrows(RestClientResponseException.class, () -> {
            employeeClient.createEmployee(employee);
        });

        assertEquals(exception, thrownException);
        verify(restTemplate, times(1)).postForObject(anyString(), any(Employee.class), eq(GetEmployeeDto.class));
    }

    @Test
    public void testCreateEmployeeRuntimeException() {
        Employee employee = new Employee("102", "Aarav Patel", 50000, 30, "");

        when(restTemplate.postForObject(anyString(), any(Employee.class), eq(GetEmployeeDto.class))).thenThrow(new RuntimeException("API error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeClient.createEmployee(employee);
        });

        assertEquals("Exception occurred while creating employee", exception.getMessage());
    }

    @Test
    public void testDeleteEmployeeByIdSuccess() {
        doNothing().when(restTemplate).delete(anyString());

        assertDoesNotThrow(() -> {
            employeeClient.deleteEmployeeById("101");
        });

        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    public void testDeleteEmployeeByIdRetryOnTooManyRequests() {
        RestClientResponseException exception = mock(RestClientResponseException.class);

        when(exception.getRawStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS.value());
        when(exception.getStatusText()).thenReturn("Too many requests");

        doThrow(exception).when(restTemplate).delete(anyString());

        Exception thrownException = assertThrows(RestClientResponseException.class, () -> {
            employeeClient.deleteEmployeeById("103");
        });

        assertEquals(exception, thrownException);
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    public void testDeleteEmployeeByIdRuntimeException() {
        doThrow(new RuntimeException("API error")).when(restTemplate).delete(anyString());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeClient.deleteEmployeeById("1");
        });

        assertEquals("Exception occurred while deleting employee", exception.getMessage());
    }
}
