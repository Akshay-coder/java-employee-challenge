package com.example.rqchallenge.employees.exceptions;

public class EmployeeNotExist extends RuntimeException {
    public EmployeeNotExist(String message) {
        super(message);
    }
}
