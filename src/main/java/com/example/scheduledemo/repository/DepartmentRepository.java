package com.example.scheduledemo.repository;

import com.example.scheduledemo.repository.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Long>, QuerydslPredicateExecutor<DepartmentEntity>
{

    Optional<DepartmentEntity> findByDingTalkDepartmentId(Long dingTalkDepartmentId);
}