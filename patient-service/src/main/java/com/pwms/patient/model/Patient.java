package com.pwms.patient.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO → IDENTITY
    private int patientId;

    private String patientName;
    private int age;

    @Column(unique = true)
    private String email;

    private String medicalHistory;
}