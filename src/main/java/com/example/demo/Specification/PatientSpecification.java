package com.example.demo.Specification;

import com.example.demo.Model.Patient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PatientSpecification {
    public static Specification<Patient> getSpecification(String name, String address, Integer StartAge, Integer EndAge) {
        return new Specification<Patient>(){

            @Override
            public @Nullable Predicate toPredicate(Root<Patient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
               List<Predicate> predicates=new ArrayList<>();
               if(name!=null && !name.isEmpty()) {
                   predicates.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
               }
               if(address!=null && !address.isEmpty()) {
                   predicates.add(criteriaBuilder.like(root.get("address").as(String.class), "%" + address + "%"));
               }
               if(StartAge!=null && EndAge!=null) {
                   predicates.add(criteriaBuilder.and(
                           criteriaBuilder.greaterThanOrEqualTo(root.get("age"), StartAge),
                           criteriaBuilder.lessThanOrEqualTo(root.get("age"), EndAge)
                   ));
               }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
