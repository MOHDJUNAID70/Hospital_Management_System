package com.example.demo.Pagination;

import com.example.demo.DTO.Patient.PatientDTO;
import com.example.demo.Repository.PatientRepo;
import com.example.demo.Service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hospital")
@Tag(name = "Patient Pagination APIs")
public class PatientPage {
    @Autowired
    PatientRepo patientRepo;
    @Autowired
    private PatientService patientService;

    @GetMapping("patient/details")
    public Page<PatientDTO> getPatients(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer StartAge,
            @RequestParam(required = false) Integer EndAge
            ) {
        Sort sort=Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(pageNo-1, pageSize, sort);
        return patientService.fetchAll(pageable, name, address, StartAge, EndAge);
    }
}
