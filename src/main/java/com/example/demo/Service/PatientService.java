package com.example.demo.Service;

import com.example.demo.ExceptionHandler.CustomException;
import com.example.demo.DTO.Patient.PatientDTO;
import com.example.demo.Mapper.PatientMapper;
import com.example.demo.Model.Patient;
import com.example.demo.Model.Users;
import com.example.demo.Redis.RedisCacheService;
import com.example.demo.Repository.PatientRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Specification.PatientSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    PatientRepo patientRepo;
    @Autowired
    UserRepo usersRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private RedisCacheService redisCacheService;

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients=patientRepo.findAll();
        if(patients.isEmpty()){
            throw new CustomException("No such patient exists");
        }
        return patients.stream().map(patientMapper::ToDTO).toList();
    }

    @Transactional
    public void addPatient(Patient patient) {
        patientRepo.save(patient);
    }

    public Patient getPatientById(int id) {
        String cacheKey=String.valueOf(id);
        Patient cached=redisCacheService.getPatientById(cacheKey);
        if(cached!=null){
            return cached;
        }
        Patient patient=patientRepo.findById(id).orElseThrow(()->new CustomException("Patient not found!!!"));
        redisCacheService.setPatientById(cacheKey,patient);
        return patient;
    }

    public Patient findPatientByageAndName(int age, String name) {
        Patient patient=patientRepo.findPatientByAgeAndName(age, name);
        if(patient==null){
            throw new CustomException("No such patient exists with age " + age + " and name " + name);
        }
        return patient;
    }

    public List<Patient> getPatientsByAge(int age) {
        List<Patient> patients=patientRepo.getPatientsByAge(age);
        if(patients.isEmpty()){
            throw new CustomException("No such patient with age " + age);
        }
        return patients;
    }

    public void deletePatientById(int id) {
        patientRepo.findById(id).orElseThrow(()-> new CustomException("No such patient Exists with id " + id));
        patientRepo.deleteById(id);
    }

    public Page<PatientDTO> fetchAll(Pageable pageable, String name, String address, Integer StartAge, Integer EndAge) {
        Specification<Patient> spec= PatientSpecification.getSpecification(name, address, StartAge, EndAge);
        Page<Patient> patients=patientRepo.findAll(spec, pageable);
        return patients.map(patientMapper::ToDTO);
    }

    public List<PatientDTO> getPatientsByUserId(Integer userId) {
        Users user=usersRepository.findById(userId).orElseThrow(()->new CustomException("User not found!!!"));
        List<Patient> patients=patientRepo.findByUser(user);
        if(patients.isEmpty()){
            throw new CustomException("No such patient exists for user id " + userId);
        }
        return patients.stream().map(patientMapper::ToDTO).toList();
    }
}
