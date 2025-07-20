package com.example.scheduledemo.repository;

import com.example.scheduledemo.repository.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository
        extends JpaRepository<EmployeeEntity, Long>, QuerydslPredicateExecutor<EmployeeEntity>
{

    Optional<EmployeeEntity> findByUnionId(String unionId);
}