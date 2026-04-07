package com.pwms.wellnessplan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "plan_assignment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"patient_id", "plan_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor

public class PlanAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignmentId;

    @Column(name = "patient_id", nullable = false)
    private int patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private WellnessPlan plan;

    @Column(nullable = false)
    private LocalDate assignedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    public enum AssignmentStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}