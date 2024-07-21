package com.example.rqchallenge.employees.client;

import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.dto.EmployeeCollectionDto;
import com.example.rqchallenge.employees.dto.GetEmployeeDto;
import com.example.rqchallenge.employees.helper.EmployeeFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class EmployeeClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    EmployeeFileUtils employeeFileUtils;


    /**
     * Fetches a list of all employees from the using RestTemplate
     * @return List of Employee object
     */
    public List<Employee> fetchAllEmployees() {
        String url = UrlConstants.ALL_EMPLOYEE_URl;

        log.info("Started fetching all employees");
        log.debug("Calling URL: {}", url);


        EmployeeCollectionDto employeeCollectionDto;
        try {
            employeeCollectionDto = restTemplate.getForObject(url, EmployeeCollectionDto.class);

            log.info("Finished fetching all employees");

            if (employeeCollectionDto != null && employeeCollectionDto.getStatus().equalsIgnoreCase("success")) {
                employeeFileUtils.saveEmployeeResponseToFile(employeeCollectionDto);
            } else {
                log.warn("[{}] Received null response from the service fetching employees from file");
               throw new RuntimeException("Received null response from the service");
            }
        } catch (Exception e) {
            log.error("[{}] Exception occurred while fetching employees. Fetching employees from file", e);
            employeeCollectionDto = employeeFileUtils.fetchEmployeeFromFile();
        }
        return employeeCollectionDto.getData();
    }

    /**
     * Send request to create employee
     * @param employee
     * @return Employee
     */
    @Retryable(value = RestClientResponseException.class, backoff = @Backoff(delay = 60000))
    public Employee createEmployee(Employee employee){

        String url = UrlConstants.CREATE_EMPLOYEE;

        log.info("Sending request to create employee: {}", employee);

        GetEmployeeDto savedEmployee;
        try {
            savedEmployee = restTemplate.postForObject(url, employee, GetEmployeeDto.class);
            log.info("Employee created successfully: {}", savedEmployee);
            return savedEmployee.getData();
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                log.error("Error creating employee: Too many requests ({}). Retrying in 1 minute", e.getStatusText(), e);
                throw e;
            } else {
                log.error("Error creating employee: {}",e.getStatusText(), e);
                throw new RuntimeException("Error creating employee: " + e.getStatusText(), e);
            }
        } catch (Exception e) {
            log.error("Exception occurred while creating employee: {}", e.getMessage(), e);
            throw new RuntimeException("Exception occurred while creating employee", e);
        }


    }

    /**
     * Send request to delete employee
     * @param id
     */
    @Retryable(value = RestClientResponseException.class, backoff = @Backoff(delay = 60000))
    public void deleteEmployeeById(String id) {
        {

            String url = UrlConstants.DELETE_EMPLOYEE + "/" + id;


            log.info("Sending request to delete employee: {}",id);

            try {
                restTemplate.delete(url);
                log.info("Employee deleted successfully: {}", id);
            } catch (RestClientResponseException e) {
                if (e.getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                    log.error("Error deleting employee: Too many requests ({}). Retrying in 1 minute",e.getStatusText(), e);
                    throw e;
                } else {
                    log.error("Error deleting employee: {}",e.getStatusText(), e);
                    throw new RuntimeException("Error deleting employee: " + e.getStatusText(), e);
                }
            } catch (Exception e) {
                log.error("Exception occurred while deleting employee: {}", e.getMessage(), e);
                throw new RuntimeException("Exception occurred while deleting employee", e);
            }


        }

    }


    /**
     * Refreshing employee data after create and delete employee from file.
     */
    @Async
    public void fetchAllEmployeesWithDelay() {
        try {
            Thread.sleep(60000);
            fetchAllEmployees();
        } catch (InterruptedException e) {
            log.error("Exception occurred while refreshing employee records", e);
            throw new RuntimeException("Exception occurred refreshing employee records", e);
        }
    }
}
