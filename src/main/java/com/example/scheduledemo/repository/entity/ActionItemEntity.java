package com.example.scheduledemo.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "action_items")
@Getter
@Setter
public class ActionItemEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    String description;

    String status;

    LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "meeting_minute_id")
    MeetingMinutesEntity meetingMinute;
}
