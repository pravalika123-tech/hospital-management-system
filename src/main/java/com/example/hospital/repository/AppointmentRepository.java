package com.example.hospital.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatientIdOrderByDateTimeAsc(String patientId);
    List<Appointment> findByDoctorIdOrderByDateTimeAsc(String doctorId);
    List<Appointment> findByStatusOrderByDateTimeAsc(AppointmentStatus status);
}
