package com.example.scheduledemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_items")
@Getter
@Setter
public class ActionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    String description;

    String status;

    LocalDateTime dueDate;
}
