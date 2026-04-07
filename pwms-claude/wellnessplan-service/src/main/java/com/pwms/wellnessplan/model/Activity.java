package com.pwms.wellnessplan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor

public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private WellnessPlan plan;

    @Column(nullable = false)
    private String activityName;
}
