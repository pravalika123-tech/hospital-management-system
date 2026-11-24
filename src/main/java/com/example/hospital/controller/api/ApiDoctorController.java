package com.example.hospital.controller.api;

import java.security.Principal;
import java.time.LocalDate;
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
import com.example.hospital.dto.MedicalRecordRequest;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.MedicalRecord;
import com.example.hospital.model.Patient;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/doctor")
@Tag(name = "Doctor", description = "Appointments and medical records for the signed-in doctor")
public class ApiDoctorController {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;

    public ApiDoctorController(UserRepository userRepository, DoctorRepository doctorRepository,
                               PatientRepository patientRepository, AppointmentRepository appointmentRepository,
                               MedicalRecordRepository recordRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository = recordRepository;
    }

    private Doctor currentDoctor(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Signed-in user not found"));
        return doctorRepository.findById(user.getLinkedId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));
    }

    @GetMapping("/appointments")
    @Operation(summary = "List appointments assigned to the current doctor")
    public List<Appointment> appointments(Principal principal) {
        Doctor doctor = currentDoctor(principal);
        return appointmentRepository.findByDoctorIdOrderByDateTimeAsc(doctor.getId());
    }

    @PatchMapping("/appointments/{id}/status")
    @Operation(summary = "Approve, complete or cancel one of your appointments")
    public Appointment updateStatus(@PathVariable String id,
                                    @Valid @RequestBody AppointmentStatusRequest request,
                                    Principal principal) {
        Doctor doctor = currentDoctor(principal);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!doctor.getId().equals(appointment.getDoctorId())) {
            throw new IllegalArgumentException("This appointment does not belong to you");
        }
        appointment.setStatus(request.status());
        return appointmentRepository.save(appointment);
    }

    @GetMapping("/records")
    @Operation(summary = "List medical records created by the current doctor")
    public List<MedicalRecord> records(Principal principal) {
        Doctor doctor = currentDoctor(principal);
        return recordRepository.findByDoctorIdOrderByDateDesc(doctor.getId());
    }

    @PostMapping("/records")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a medical record for a patient")
    public MedicalRecord createRecord(@Valid @RequestBody MedicalRecordRequest request, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        MedicalRecord record = new MedicalRecord(patient.getId(), patient.getName(),
                doctor.getId(), doctor.getName(), LocalDate.now(),
                request.diagnosis(), request.prescription(), request.notes());
        return recordRepository.save(record);
    }
}
