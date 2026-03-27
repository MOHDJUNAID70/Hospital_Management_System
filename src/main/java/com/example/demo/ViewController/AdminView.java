package com.example.demo.ViewController;

import com.example.demo.Enum.AppointmentStatus;
import com.example.demo.Enum.DoctorSpecializations;
import com.example.demo.ExceptionHandler.CustomException;
import com.example.demo.DTO.AppointmentDTO;
import com.example.demo.DTO.Patient.PatientDTO;
import com.example.demo.DTO.DoctorAvailability.SetDoctorAvailabilityDTO;
import com.example.demo.DTO.DoctorAvailability.UpdateAvailabilityDTO;
import com.example.demo.Model.Doctor;
import com.example.demo.Model.DoctorAvailability;
import com.example.demo.Pagination.AppointmentPage;
import com.example.demo.Repository.DoctorAvailabilityRepo;
import com.example.demo.Service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminView {
    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;
    @Autowired
    private DoctorAvailabilityRepo doctorAvailabilityRepo;
    @Autowired
    private AppointmentService appointmentService;

    //    DoctorInfo
    @GetMapping("/doctors/info")
    public String doctorInfo(
            Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "id") String sortBy,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer minExperience,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer maxExperience,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String name,
            @org.springframework.web.bind.annotation.RequestParam(required = false) DoctorSpecializations specialization
    ) {
        Sort sort= Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(pageNo-1,pageSize,sort);
        Page<Doctor> doctors = doctorService.fetchAll(pageable, minExperience, maxExperience, name, specialization);
        model.addAttribute("doctors", doctors);
        model.addAttribute("name", name);
        model.addAttribute("specialization", specialization);
        model.addAttribute("minExperience", minExperience);
        model.addAttribute("maxExperience", maxExperience);
        return "doctor/doctorInfo";
    }

    @PostMapping("/doctors/add")
    public String addDoctor(@Valid @ModelAttribute Doctor doctor){
        doctorService.addDoctor(doctor);
        return "redirect:/admin/doctors/info";
    }

    @PostMapping("/doctors/edit")
    public String editDoctor(@Valid @ModelAttribute Doctor doctor) {
        doctorService.updateDoctorInfo(doctor);
        return "redirect:/admin/doctors/info";
    }

    @PostMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable int id) {
        doctorService.deleteById(id);
        return "redirect:/admin/doctors/info";
    }

    @GetMapping("/doctors/{id}/appointments")
    public String doctorAppointments(@PathVariable int id, Model model){
        try {
            List<AppointmentDTO> appointments=appointmentService.appointmentbyDoctorId(id);
            model.addAttribute("appointments", appointments);
        } catch (CustomException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointments", Collections.emptyList());
        }
        return "doctor/DoctorAppointments";
    }

    @GetMapping("/doctors/{id}/availability")
    public String doctorAvailability(@PathVariable int id, Model model){
        model.addAttribute("doctorId", id);
        List<DoctorAvailability> availabilities=doctorAvailabilityRepo.findByDoctorId(id);
        if(availabilities == null || availabilities.isEmpty()){
            model.addAttribute("availabilities", Collections.emptyList());
            model.addAttribute("errorMessage", "No availability found for this doctor.");
            return "doctor/doctorAvailability";
        }
        model.addAttribute("availabilities", availabilities);
        return "doctor/doctorAvailability";
    }

    @PostMapping("/doctors/{id}/update_availability")
    public String updateDoctorAvailability(@PathVariable int id, @ModelAttribute UpdateAvailabilityDTO request) {
        doctorAvailabilityService.updateDoctorAvailability(request);
        return "redirect:/admin/doctors/" + id + "/availability";
    }

    @PostMapping("/doctors/{id}/add_availability")
    public String addDoctorAvailability(@PathVariable int id, @ModelAttribute SetDoctorAvailabilityDTO availability) {
        doctorAvailabilityService.setAvailability(availability);
        return "redirect:/admin/doctors/" + id + "/availability";
    }

    @PostMapping("/doctors/{id}/delete_availability/{availabilityId}")
    public String deleteDoctorAvailability(@PathVariable int id, @PathVariable int availabilityId){
        doctorAvailabilityService.deleteAvailability(availabilityId);
        return "redirect:/admin/doctors/" + id + "/availability";
    }

//    PatientInfo
@GetMapping("/patients/info")
public String patientInfo(
        Model model,
        @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "1") Integer pageNo,
        @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "id") String sortBy,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String name,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String address,
        @org.springframework.web.bind.annotation.RequestParam(required = false) Integer StartAge,
        @org.springframework.web.bind.annotation.RequestParam(required = false) Integer EndAge
) {
        Sort sort= Sort.by(sortBy).ascending();
    Pageable pageable= PageRequest.of(pageNo-1,pageSize,sort);
    Page<PatientDTO> patients = patientService.fetchAll(pageable, name, address, StartAge, EndAge);
    model.addAttribute("patients", patients);
    model.addAttribute("name", name);
    model.addAttribute("address", address);
    model.addAttribute("StartAge", StartAge);
    model.addAttribute("EndAge", EndAge);
    return "patient/patientInfo";
}

    @GetMapping("/patients/info1")
    public String patientInfo(Model model){
        List<PatientDTO> patients=patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patient/patientData";
    }

    @Autowired
    AppointmentPage appointmentPage;
    //Appointments
    @GetMapping("/appointments/info")
    public String appointmentInfo(Model model,
                                @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "appointmentDate") String sortBy,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) LocalDate appointmentDate,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) AppointmentStatus status,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) LocalTime appointmentStartTime,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) LocalTime appointmentEndTime
                                ){
        Sort sort= Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(pageNo-1,pageSize,sort);
        Page<AppointmentDTO> appointments=appointmentService.fetchAll(pageable, appointmentDate, status, appointmentStartTime, appointmentEndTime);
        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentDate", appointmentDate);
        model.addAttribute("status", status);
        model.addAttribute("appointmentStartTime", appointmentStartTime);
        model.addAttribute("appointmentEndTime", appointmentEndTime);
        return "appointments/appointmentInfo";
    }

    @GetMapping("/appointment/details/{id}")
    public String appointmentDetails(@PathVariable int id, Model model){
        AppointmentDTO appointmentDTO=appointmentService.getAppointmentsById(id);
        model.addAttribute("appointmentDTO", appointmentDTO);
        return "appointments/appointmentDetails";
    }
}


