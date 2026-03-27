package com.example.demo.ViewController;

import com.example.demo.DTO.AppointmentBookingForm;
import com.example.demo.DTO.AppointmentDTO;
import com.example.demo.Model.Doctor;
import com.example.demo.Model.Patient;
import com.example.demo.Service.AppointmentService;
import com.example.demo.Users.CurrentUser;
import com.example.demo.Repository.DoctorRepo;
import com.example.demo.Repository.PatientRepo;
import com.example.demo.ExceptionHandler.CustomException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("patient")
public class PatientView {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private CurrentUser currentUser;

    @GetMapping("/book-appointment")
    public String bookAppointment(Model model) {
        if (!model.containsAttribute("appointmentForm")) {
            model.addAttribute("appointmentForm", new AppointmentBookingForm());
        }
        addBookingDropdownData(model);
        return "appointments/BookAppointments";
    }

    @PostMapping("/appointment-booked")
    public String appointmentBooked(@Valid @ModelAttribute("appointmentForm") AppointmentBookingForm appointmentForm,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addBookingDropdownData(model);
            return "appointments/BookAppointments";
        }

        try {
            appointmentService.bookAppointmentForUser(
                    appointmentForm.getDoctorId(),
                    appointmentForm.getPatientId(),
                    appointmentForm.getAppointmentDate(),
                    appointmentForm.getStartTime(),
                    currentUser.getCurrentUser()
            );
            redirectAttributes.addFlashAttribute("bookingSuccess", "Appointment booked successfully");
        } catch (RuntimeException e) {
            model.addAttribute("bookingError", e.getMessage());
            addBookingDropdownData(model);
            return "appointments/BookAppointments";
        }
        return "redirect:/patient/book-appointment";
    }

    private void addBookingDropdownData(Model model) {
        List<Doctor> availableDoctors = doctorRepo.findAll();
        List<Patient> userPatients = patientRepo.findByUser(currentUser.getCurrentUser());
        List<LocalTime> availableTimeSlots = generateTimeSlots();

        model.addAttribute("availableDoctors", availableDoctors);
        model.addAttribute("userPatients", userPatients);
        model.addAttribute("availableTimeSlots", availableTimeSlots);
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);      // 9:00 AM
        LocalTime endTime = LocalTime.of(17, 0);       // 5:00 PM
        LocalTime lunchStart = LocalTime.of(13, 0);    // 1:00 PM
        LocalTime lunchEnd = LocalTime.of(14, 0);      // 2:00 PM

        LocalTime current = startTime;
        while (current.isBefore(endTime)) {
            // Skip lunch time slots
            if (!(current.isAfter(lunchStart) && current.isBefore(lunchEnd))) {
                slots.add(current);
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @GetMapping("/appointments")
    public String viewMyAppointments(Model model) {
        try {
            List<AppointmentDTO> userAppointments = appointmentService.getUserAppointments(currentUser.getCurrentUser());
            model.addAttribute("appointments", userAppointments);
        } catch (CustomException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointments", Collections.emptyList());
        }
        return "appointments/myAppointments";
    }
}

