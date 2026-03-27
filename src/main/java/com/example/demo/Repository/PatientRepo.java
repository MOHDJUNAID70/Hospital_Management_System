package com.example.demo.Repository;

import com.example.demo.Enum.Gender;
import com.example.demo.Model.Patient;
import com.example.demo.Model.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Integer>, JpaSpecificationExecutor<Patient> {

    Patient findPatientByAgeAndName(int age, String name);

    List<Patient> getPatientsByAge(int age);

    Page<Patient> findByGender(Pageable pageable, Gender gender, Specification<Patient> specification);


    List<Patient> findByAgeIsGreaterThanEqual(Integer age);

    Patient findByNameAndUser(@NotBlank @Size(min = 3, max = 50) @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must only contain alphabets and space") String name, Users user);

    List<Patient> findByUser(Users user);
}
