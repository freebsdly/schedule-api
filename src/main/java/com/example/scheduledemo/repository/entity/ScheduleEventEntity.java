package com.example.scheduledemo.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "schedule_events")
public class ScheduleEventEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "dingtalk_event_id")
    private String dingtalkEventId;

    @Column(nullable = false)
    private String summary;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String location;

    @ManyToOne
    @JoinColumn(name = "organizer_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private EmployeeEntity organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttendeeEntity> attendees;

    private Boolean isAllDay;

    private String importance;

    private String priority;

    private String status;
}
