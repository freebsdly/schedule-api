package com.example.scheduledemo.repository;

import com.example.scheduledemo.repository.entity.ScheduleEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleEventRepository extends JpaRepository<ScheduleEventEntity, Long>, QuerydslPredicateExecutor<ScheduleEventEntity> {

    void deleteByDingtalkEventId(String id);

    Optional<ScheduleEventEntity> findByDingtalkEventId(String id);
}