package com.example.scheduledemo.repository;

import com.example.scheduledemo.entity.ScheduleEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleEventRepository extends JpaRepository<ScheduleEventEntity, Long>, QuerydslPredicateExecutor<ScheduleEventEntity> {
}