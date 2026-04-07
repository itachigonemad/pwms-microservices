package com.pwms.wellnessplan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wellness_plan")
@Getter
@Setter
@NoArgsConstructor
public class WellnessPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    @Column(nullable = false)
    private String planName;

    @OneToMany(mappedBy = "plan",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JsonIgnore
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "plan",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PlanAssignment> assignments = new ArrayList<>();
}
