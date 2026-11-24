package com.example.hospital.controller.api;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.hospital.dto.AppointmentStatusRequest;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Patient;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administrator", description = "Hospital-wide administration")
public class ApiAdminController {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public ApiAdminController(DoctorRepository doctorRepository, PatientRepository patientRepository,
                              AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/doctors")
    @Operation(summary = "List all doctors")
    public List<Doctor> doctors() {
        return doctorRepository.findAll();
    }

    @PostMapping("/doctors")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new doctor")
    public Doctor addDoctor(@Valid @RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @GetMapping("/patients")
    @Operation(summary = "List all patients")
    public List<Patient> patients() {
        return patientRepository.findAll();
    }

    @GetMapping("/appointments")
    @Operation(summary = "List all appointments")
    public List<Appointment> appointments() {
        return appointmentRepository.findAll();
    }

    @PatchMapping("/appointments/{id}/status")
    @Operation(summary = "Update any appointment's status")
    public Appointment updateStatus(@PathVariable String id,
                                    @Valid @RequestBody AppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(request.status());
        return appointmentRepository.save(appointment);
    }
}
