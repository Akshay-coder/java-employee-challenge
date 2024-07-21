package com.example.rqchallenge.employees.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeCollectionDto extends BaseResponse{

    private List<Employee> data;
}
