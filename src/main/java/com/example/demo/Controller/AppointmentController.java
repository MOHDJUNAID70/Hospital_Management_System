package com.example.demo.Controller;

import com.example.demo.DTO.Doctor.DoctorDTO;
import com.example.demo.DTO.Patient.PatientDTO;
import com.example.demo.DTO.User.UserSummaryDTO;
import com.example.demo.ExceptionHandler.CustomException;
import com.example.demo.Locking.OptimisticLocking;
import com.example.demo.DTO.AppointmentDTO;
import com.example.demo.Model.Appointment;
import com.example.demo.Repository.AppointmentRepo;
import com.example.demo.Service.AppointmentService;
import com.example.demo.Users.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("hospital")
@Tag(name = "Appointment APIs")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private OptimisticLocking optimisticLocking;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping("/all_appointments")
    public List<AppointmentDTO> getAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointment_fetched_by_doctor_id")
    public List<AppointmentDTO> appointmentbyDoctorId(@RequestParam("id") int doctor_id){
        return appointmentService.appointmentbyDoctorId(doctor_id);
    }

    @GetMapping("/appointment_fetched_by_date")
    public List<AppointmentDTO> appointmentbyDate(@RequestParam("date") LocalDate date){
        return appointmentService.appointmentbyDate(date);
    }

    @GetMapping("/book_appoint")
    public ResponseEntity<String> OptimisticBooking() throws InterruptedException {
        optimisticLocking.TestOptimisticLocking();
        return new ResponseEntity<>("Appointment booked successfully", HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('Patient')")
    @PostMapping("/book_appointment")
    public ResponseEntity<String> bookAppointment(@RequestHeader("Idempotency-Key") String key,
            @RequestBody @Valid Appointment appointment){
        appointmentService.BookTheAppointmentWithIdempotency(key, appointment);
        return new ResponseEntity<>("Your Appointment has been Booked", HttpStatus.OK);
    }

    @PostMapping("/appointment/booking")
    public ResponseEntity<String> bookAppointmentWithIdempotencyCheckThroughRedis(@RequestHeader("Idempotency-Key") String key,
                                                  @RequestBody @Valid Appointment appointment){
        appointmentService.BookWithIdempotency(key, appointment);
        return new ResponseEntity<>("Your Appointment has been Booked", HttpStatus.OK);
    }

    @DeleteMapping("appointment_deleted_with_date")
    public ResponseEntity<String> deleteAppointmentByDate(@RequestParam("date") LocalDate date){
        appointmentService.deleteAppointmentByDate(date);
        return new ResponseEntity<>("Your Appointment has been Deleted", HttpStatus.OK);
    }

    @DeleteMapping("delete_by_Id")
    public ResponseEntity<String> deleteAppointmentById(@RequestParam("id") Integer id){
        appointmentService.deleteAppointmentById(id);
        return new ResponseEntity<>("Your Appointment has been Deleted", HttpStatus.OK);
    }

    @GetMapping("/get-appointment-by-Id/{id}")
    public AppointmentDTO getAppointmentById(@PathVariable("id") Integer id){
        return appointmentService.getAppointmentsById(id);
    }
}
