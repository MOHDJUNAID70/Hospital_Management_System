package com.example.demo.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentBookingForm {

    @NotNull(message = "Please select a doctor")
    private Integer doctorId;

    @NotNull(message = "Please select a patient")
    private Integer patientId;

    @NotNull(message = "Please select an appointment date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointmentDate;

    @NotNull(message = "Please select a start time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;
}

