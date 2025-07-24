package com.example.scheduledemo.repository;

import com.example.scheduledemo.repository.entity.EventAttendeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendeeEntity, Long>, QuerydslPredicateExecutor<EventAttendeeEntity> {
}