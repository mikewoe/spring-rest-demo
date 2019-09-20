package com.mikewoe.springrestdemo.employee;

public class EmployeeNotFoundException extends RuntimeException {

    EmployeeNotFoundException(Long id) {
        super("Could not find employee with " + id + " id");
    }
}
