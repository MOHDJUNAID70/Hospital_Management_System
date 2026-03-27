package com.example.demo.Repository;

import com.example.demo.Enum.AppointmentStatus;
import com.example.demo.Model.Appointment;
import com.example.demo.Model.Doctor;
import com.example.demo.Model.Patient;
import com.example.demo.Model.Users;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

//    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
//            Doctor doctor,
//            LocalDate appointmentDate,
//            LocalTime appointmentTime
//    );

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeLessThanAndAppointmentEndTimeGreaterThan(
            Doctor doctor,
            LocalDate appointmentDate,
            LocalTime end,
            LocalTime appointmentTime
    );

    List<Appointment> findByDoctorId(int Id);

    List<Appointment> findByappointmentDate(LocalDate appointmentDate);

    void deleteAppointmentByAppointmentDate(LocalDate date);

    boolean existsByDoctorAndPatientAndAppointmentDate(Doctor doctor, Patient patient,
                                                       LocalDate appointmentDate);

    boolean existsByPatientAndAppointmentDateAndAppointmentTimeLessThanAndAppointmentEndTimeGreaterThan(Patient patient,
             @NotNull LocalDate appointmentDate, LocalTime end, LocalTime appointmentTime);

    Optional< List<Appointment>> findByDoctorAndAppointmentDateAndStatusAndAppointmentTimeBeforeOrAppointmentEndTimeAfter(Doctor doctor, LocalDate localDate, AppointmentStatus appointmentStatus, @NotNull LocalTime startTime, @NotNull LocalTime endTime);

    Page<Appointment> findByStatus(Pageable pageable, AppointmentStatus status);
    Appointment findByAppointmentDate(LocalDate date);

    long countByAppointmentDate(LocalDate date);

    List<Appointment> findByUser(Users user);

    Page<Appointment> findByUser(Users user, Pageable pageable);
}
