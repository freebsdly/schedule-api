package com.example.scheduledemo.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "meeting_minutes")
public class MeetingMinutesEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String rawData;

    private String answer;

    @OneToMany(mappedBy = "meetingMinute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionItemEntity> actionItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ScheduleEventEntity event;

    private String meetingMinutesUrl;

}