package com.example.demo.Controller;

import com.example.demo.DTO.AppointmentDTO;
import com.example.demo.Mapper.AppointMapper;
import com.example.demo.Model.Appointment;
import com.example.demo.Repository.AppointmentRepo;
import com.example.demo.Repository.DoctorRepo;
import com.example.demo.Repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private AppointMapper appointMapper;

    @GetMapping("/")
    public String index() {
        return "layouts/index";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        long doctorCount = doctorRepo.count();
        long patientCount = patientRepo.count();
        long appointmentCount = appointmentRepo.count();

        // Today's appointments count
        LocalDate today = LocalDate.now();
        long todayAppointments = appointmentRepo.countByAppointmentDate(today);

        // Recent 5 appointments (latest first)
        List<Appointment> recentList = appointmentRepo.findAll(
                PageRequest.of(0, 5, Sort.by("appointmentDate").descending()
                        .and(Sort.by("appointmentTime").descending()))
        ).getContent();
        List<AppointmentDTO> recentAppointments = recentList.stream()
                .map(appointMapper::ToDTO).toList();

        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("appointmentCount", appointmentCount);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("recentAppointments", recentAppointments);
        return "dashboard/admin";
    }

    @GetMapping("/doctor/dashboard")
    public String doctor() {
        return "dashboard/doctor";
    }

    @GetMapping("/patient/dashboard")
    public String patients() {
        return "dashboard/patient";
    }

    @GetMapping("/logins")
    public String loginPage() {
        return "auth/loginPage";
    }

    @GetMapping("/reg")
    public String registerPage() {
        return "auth/register";
    }
}
