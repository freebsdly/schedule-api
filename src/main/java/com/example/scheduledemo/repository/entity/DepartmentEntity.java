package com.example.scheduledemo.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "departments")
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(name = "dingtalk_dept_id")
    private Long dingTalkDepartmentId;

    @Column(name = "parent_id")
    private Long dingTalkParentId;

}