package com.example.scheduledemo.repository;

import com.example.scheduledemo.entity.ActionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItemEntity, Long>, QuerydslPredicateExecutor<ActionItemEntity> {
}
