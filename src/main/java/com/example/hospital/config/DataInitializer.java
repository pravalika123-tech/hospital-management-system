package com.example.hospital.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.MedicalRecord;
import com.example.hospital.model.Patient;
import com.example.hospital.model.Role;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DoctorRepository doctorRepository,
                           PatientRepository patientRepository, AppointmentRepository appointmentRepository,
                           MedicalRecordRepository recordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository = recordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(new User("admin", passwordEncoder.encode("admin123"),
                "admin@hospital.com", Role.ADMIN, true, null));

        Doctor cardiologist = doctorRepository.save(new Doctor(
                "Dr. Ananya Rao", "Cardiology", "ananya@hospital.com", "9876500001",
                12, 800.0, true));
        userRepository.save(new User("doctor", passwordEncoder.encode("doctor123"),
                "ananya@hospital.com", Role.DOCTOR, true, cardiologist.getId()));

        Doctor neurologist = doctorRepository.save(new Doctor(
                "Dr. Vikram Sethi", "Neurology", "vikram@hospital.com", "9876500002",
                9, 950.0, true));
        userRepository.save(new User("vikram", passwordEncoder.encode("doctor123"),
                "vikram@hospital.com", Role.DOCTOR, true, neurologist.getId()));

        Patient patient = patientRepository.save(new Patient(
                "Sneha Sharma", "sneha@example.com", "9876500010", "1996-04-12",
                "Hyderabad", "patient"));
        userRepository.save(new User("patient", passwordEncoder.encode("patient123"),
                "sneha@example.com", Role.PATIENT, true, patient.getId()));

        appointmentRepository.save(new Appointment(
                patient.getId(), patient.getName(), cardiologist.getId(), cardiologist.getName(),
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0),
                "Routine heart check-up", AppointmentStatus.APPROVED));

        appointmentRepository.save(new Appointment(
                patient.getId(), patient.getName(), neurologist.getId(), neurologist.getName(),
                LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0),
                "Recurring headache", AppointmentStatus.PENDING));

        recordRepository.save(new MedicalRecord(
                patient.getId(), patient.getName(), cardiologist.getId(), cardiologist.getName(),
                LocalDate.now().minusDays(10), "Mild palpitations", " Tablet Metoprolol 25mg once daily",
                "Patient responded well to initial treatment."));
    }
}
