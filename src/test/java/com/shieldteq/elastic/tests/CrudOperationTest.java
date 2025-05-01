package com.shieldteq.elastic.tests;

import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.model.Employee;
import com.shieldteq.elastic.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class CrudOperationTest extends AbstractITTests {
    @Autowired
    private EmployeeRepository repository;

    @Test
    public void crudTests() {
        Employee employee = Employee.builder().id("employee1").name("sam").age(30).build();
        repository.save(employee);

        Employee fetch = repository.findById("employee1").orElseThrow();
        Assertions.assertEquals("sam", fetch.name());
        Assertions.assertEquals(30, fetch.age());

        employee = Employee.builder().id("employee1").name("sam").age(32).build();
        fetch = repository.save(employee);
        Assertions.assertEquals(32, fetch.age());

        repository.deleteById("employee1");
        Assertions.assertTrue(repository.findById("employee1").isEmpty());
    }

    @Test
    public void bulkCreate() {
        List<Employee> employees = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Employee.builder().id("e" + i).name("name_" + i).age(30 + i).build())
                .toList();
        repository.saveAll(employees);

        Assertions.assertEquals(10, repository.count());

        List<String> ids = List.of("e1", "e5", "e7");
        Iterable<Employee> allById = repository.findAllById(ids);
        List<Employee> find = Streamable.of(allById).toList();
        Assertions.assertEquals(3, find.size());

        List<Employee> updated = find.stream().map(e -> Employee.builder().id(e.id()).name(e.name()).age(e.age() + 1).build()).toList();
        repository.saveAll(updated);

        Assertions.assertEquals(10, repository.count());

        repository.deleteAllById(ids);

        Assertions.assertEquals(7, repository.count());

    }

}
