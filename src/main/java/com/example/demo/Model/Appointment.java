package com.example.demo.Model;

import com.example.demo.Enum.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appointmentid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    @NotNull
    private LocalDate appointmentDate;
    @NotNull
    private LocalTime appointmentTime;
    private LocalTime appointmentEndTime;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private Timestamp StartTs;
    private Timestamp EndTs;

    @PrePersist
    public void prePersist(){
        status=AppointmentStatus.Booked;
    }
}
