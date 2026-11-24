package com.example.hospital.controller.api;

import java.security.Principal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.hospital.dto.BookAppointmentRequest;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Patient;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;
import com.example.hospital.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/patient")
@Tag(name = "Patient", description = "Patient profile, doctors and appointment booking")
public class ApiPatientController {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    public ApiPatientController(UserRepository userRepository, PatientRepository patientRepository,
                                DoctorRepository doctorRepository, AppointmentRepository appointmentRepository,
                                AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    private Patient currentPatient(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Signed-in user not found"));
        return patientRepository.findById(user.getLinkedId())
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found"));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the current patient's profile")
    public Patient profile(Principal principal) {
        return currentPatient(principal);
    }

    @GetMapping("/doctors")
    @Operation(summary = "List doctors available for booking")
    public List<Doctor> doctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/appointments")
    @Operation(summary = "List the current patient's appointments")
    public List<Appointment> appointments(Principal principal) {
        Patient patient = currentPatient(principal);
        return appointmentRepository.findByPatientIdOrderByDateTimeAsc(patient.getId());
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book an appointment (409 when the slot conflicts)")
    public Appointment book(@Valid @RequestBody BookAppointmentRequest request, Principal principal) {
        Patient patient = currentPatient(principal);
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        return appointmentService.book(patient.getId(), patient.getName(), doctor.getId(), doctor.getName(),
                request.dateTime(), request.reason());
    }
}
