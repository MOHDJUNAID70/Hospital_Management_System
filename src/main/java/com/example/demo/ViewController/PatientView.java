package com.example.demo.ViewController;

import com.example.demo.Model.Patient;
import com.example.demo.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("patient")
public class PatientView {


    @GetMapping("/book-appointment")
    public String bookAppointment(Model model) {
        return "appointments/BookAppointments";
    }
}
