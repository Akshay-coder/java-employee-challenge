package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.client.EmployeeClient;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.exceptions.EmployeeNotExist;
import com.example.rqchallenge.employees.exceptions.InvalidIdException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    EmployeeClient employeeClient;

    /**
     * Fetches a list of all employees from the employee client.
     * @return list of Employee object
     */
    public List<Employee> getAllEmployees() {
        log.info("Started fetching all employees");

        List<Employee> employeeList = null;
        try {
            employeeList = employeeClient.fetchAllEmployees();
            log.info("Successfully fetched all employees");
            return employeeList;
        } catch (Exception e) {
            log.error("Error occurred while fetching employees: ",e);
            throw new RuntimeException("Error occurred while fetching employees");
        }
    }

    /**
     * Searching all employees by name
     * @param searchString
     * @return list of Employee object
     */
    public List<Employee> searchEmployeesByName(String searchString) {
        log.info("Started fetching all employees by name {}",searchString);

        List<Employee> employeeList = null;
        try {
            employeeList = employeeClient.fetchAllEmployees();

            if (employeeList == null || employeeList.isEmpty()) {
                return new ArrayList<>();
            }

            employeeList = employeeList.stream().filter(employee -> employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());
            log.info("Successfully fetched all employees by name {}",searchString);
            return employeeList;
        } catch (Exception e) {
            log.error("Error occurred while fetching employees by name {}",searchString,e);
            throw new RuntimeException("Error occurred while fetching employees by name "+searchString);
        }


    }

    /**
     * get employee by id
     * @param id
     * @return Employee
     */
    public Employee getEmployeeById(String id) {

        log.info("Started fetching employee by id {}", id);
        if (!NumberUtils.isNumber(id)) {
            log.error("Invalid ID format. ID should be a number: {}", id);
            throw new InvalidIdException("Invalid ID format. ID should be a number.");
        }

        List<Employee> employeeList = null;
        Optional<Employee> employeeById = Optional.empty();
        try {
            employeeList = employeeClient.fetchAllEmployees();
            employeeById = employeeList.stream().filter(employee -> employee.getId().equalsIgnoreCase(id)).findFirst();
            log.info("Successfully fetched employee by id {}", id);
            if (employeeById.isPresent()) {
                return employeeById.get();
            } else {
                log.error("Employee with ID {} does not exist", id);
                throw new EmployeeNotExist(String.format("Employee with ID %s does not exist", id));
            }
        } catch (EmployeeNotExist e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while fetching employee by id {} : " + e.getMessage(), id);
            throw new RuntimeException("Error occurred while fetching employee by id " + id);
        }
    }

    /**
     * Get highest salary
     * @return Integer
     */
    public Integer getHighestSalaryOfEmployee() {

        log.info("Started fetching highest salary");

        List<Employee> employeeList = null;
        Optional<Employee> employeeById = Optional.empty();
        try {
            employeeList = employeeClient.fetchAllEmployees();
            if(employeeList == null || employeeList.isEmpty()) {
                String errorMessage = "No employees found in the list";
                log.error("{}", errorMessage);
                throw new EmployeeNotExist(errorMessage);
            }
            employeeList = sortBySalary(employeeList);
            employeeById = employeeList.stream().findFirst();
            log.info("Successfully fetched employee highest salary {}",employeeById.get().getEmployeeSalary());
            return employeeById.get().getEmployeeSalary();
        } catch (EmployeeNotExist e) {
            throw e;
        }catch (Exception e) {
            log.error("Error occurred while fetching highest salary: " + e.getMessage());
            throw new RuntimeException("Error occurred while fetching highest salary");
        }


    }

    /**
     * Sort Employee list by salary in descending order
     * @param employees
     * @return List of Employee object
     */
    private List<Employee> sortBySalary(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Search all employees with top ten salary
     * @return List of String
     */
    public List<String> getTopTenHighestSalaryEmployeeNames() {
        log.info("Started fetching all employees with top ten salary");


        List<Employee> employeeList = null;
        try {
            employeeList = employeeClient.fetchAllEmployees();

            if(employeeList == null || employeeList.isEmpty()) {
                String errorMessage = "No employees found in the list";
                log.error("{}", errorMessage);
                throw new EmployeeNotExist(errorMessage);
            }

            employeeList = sortBySalary(employeeList);
            employeeList = employeeList.subList(0, Math.min(10, employeeList.size()));
            log.info("Successfully fetched all employees with top ten salary");
            return employeeList.stream().map(Employee::getEmployeeName).collect(Collectors.toList());

        } catch (EmployeeNotExist e) {
            throw e;
        }catch (Exception e) {
            log.error("Error occurred while fetching all employees with top ten salary: " + e.getMessage());
            throw new RuntimeException("Error occurred while fetching all employees with top ten salary");
        }


    }

    /**
     * Creates employee from input
     * @param employeeInput
     * @return Employee
     */
    public Employee createEmployee(Map<String, Object> employeeInput) {
        log.info("Creating employee");
        log.debug("Creating employee from input: {}", employeeInput);
        Employee employee = new Employee();
        try {
            employee.setEmployeeName((String) employeeInput.get("employee_name"));
            employee.setEmployeeSalary((Integer) employeeInput.get("employee_salary"));
            employee.setEmployeeAge((Integer) employeeInput.get("employee_age"));
            employee.setProfileImage((String) employeeInput.get("profile_image"));
            if(employee.getProfileImage() == null){
                employee.setProfileImage("");
            }
        } catch (Exception e) {
            log.error("Error converting map to Employee: Invalid data types", e);
            throw new ValidationException("Error converting map to Employee: Invalid data types", e);
        }



        try {
            employee.validate(employee.getEmployeeName());
            Employee savedEmployee = employeeClient.createEmployee(employee);
            log.info("Employee saved successfully: {} ", savedEmployee);
            // Note: The getEmployee method fetches static data does not change while create and delete.
            // For consistency, the file is refreshed whenever a new employee is added or deleted,
            // considering these operations as changes to the static data.
            employeeClient.fetchAllEmployeesWithDelay();
            return savedEmployee;
        } catch (Exception e) {
            log.error("Failed to save employee:", e);
            throw e;
        }

    }

    /**
     * Delete employee by id
     * @param id
     */
    public void deleteEmployee(String id) {
        log.info("Started deleting employee by id {}", id);

        try {
            employeeClient.deleteEmployeeById(id);
            // Note: The getEmployee method fetches static data does not change while create and delete.
            // For consistency, the file is refreshed whenever a new employee is added or deleted,
            // considering these operations as changes to the static data.
            employeeClient.fetchAllEmployeesWithDelay();
        } catch (Exception e) {
            log.error("Error occurred while deleting employee by id {} : ",id,e);
            throw new RuntimeException("Error occurred while deleting employee by id " + id);
        }

        log.info("Successfully deleted employee by id {}", id);
    }
}
