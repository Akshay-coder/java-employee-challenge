package com.example.rqchallenge.employees.dto;

import lombok.Data;

@Data
public class GetEmployeeDto extends BaseResponse{

    private Employee data;
}
