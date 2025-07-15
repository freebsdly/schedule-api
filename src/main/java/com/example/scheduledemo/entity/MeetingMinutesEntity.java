package com.example.scheduledemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "meeting_minutes")
public class MeetingMinutesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String rawData;

    @OneToMany(mappedBy = "meetingMinute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionItemEntity> actionItems = new ArrayList<>();

}