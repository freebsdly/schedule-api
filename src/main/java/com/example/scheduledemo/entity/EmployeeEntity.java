package com.example.scheduledemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String badge;

    private String unionId;

    @ManyToOne
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DepartmentEntity department;

}
