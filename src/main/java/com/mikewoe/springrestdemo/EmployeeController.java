package com.mikewoe.springrestdemo;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final EmployeeResourceAssembler employeeResourceAssembler;

    EmployeeController(EmployeeRepository employeeRepository, EmployeeResourceAssembler employeeResourceAssembler) {
        this.employeeRepository = employeeRepository;
        this.employeeResourceAssembler = employeeResourceAssembler;
    }

    @GetMapping("/employees")
    Resources<Resource<Employee>> all() {
        List<Employee> employees = employeeRepository.findAll();

        List<Resource<Employee>> resources = employees.stream()
                .map(employeeResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(resources, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {
        Resource<Employee> resource = employeeResourceAssembler.toResource(
                employeeRepository.save(newEmployee)
        );

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @GetMapping("/employees/{id}")
    Resource<Employee> one(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return employeeResourceAssembler.toResource(employee);
    }

    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) throws URISyntaxException {
        Employee updatedEmployee = employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());

                    return  employeeRepository.save(employee);
                }).orElseGet(() -> {
                    newEmployee.setId(id);

                    return employeeRepository.save(newEmployee);
                });

        Resource<Employee> resource = employeeResourceAssembler.toResource(updatedEmployee);

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
