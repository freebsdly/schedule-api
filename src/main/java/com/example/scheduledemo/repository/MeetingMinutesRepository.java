package com.example.scheduledemo.repository;

import com.example.scheduledemo.repository.entity.MeetingMinutesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingMinutesRepository extends JpaRepository<MeetingMinutesEntity, Long>, QuerydslPredicateExecutor<MeetingMinutesEntity> {
}