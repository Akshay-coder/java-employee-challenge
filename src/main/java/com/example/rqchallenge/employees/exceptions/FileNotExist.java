package com.example.rqchallenge.employees.exceptions;

public class FileNotExist extends RuntimeException {

    public FileNotExist(String message) {
        super(message);
    }
}
