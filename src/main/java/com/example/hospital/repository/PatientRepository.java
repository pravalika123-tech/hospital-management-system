package com.example.hospital.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.hospital.model.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByUsername(String username);
}
