package com.shieldteq.elastic.repository;

import com.shieldteq.elastic.model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
    Pageable id(String id);
}
