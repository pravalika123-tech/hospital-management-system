package com.example.hospital.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.hospital.model.MedicalRecord;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientIdOrderByDateDesc(String patientId);
    List<MedicalRecord> findByDoctorIdOrderByDateDesc(String doctorId);
}
