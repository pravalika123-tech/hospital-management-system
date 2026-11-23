package com.example.hospital.controller;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Patient;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;

    public PatientController(UserRepository userRepository, PatientRepository patientRepository,
                             DoctorRepository doctorRepository, AppointmentRepository appointmentRepository,
                             MedicalRecordRepository recordRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository = recordRepository;
    }

    private Patient currentPatient(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Signed-in user not found"));
        return patientRepository.findById(user.getLinkedId())
                .orElseThrow(() -> new IllegalStateException("Patient profile not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Patient patient = currentPatient(principal);
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentRepository.findByPatientIdOrderByDateTimeAsc(patient.getId()));
        model.addAttribute("records", recordRepository.findByPatientIdOrderByDateDesc(patient.getId()));
        return "patient/dashboard";
    }

    @GetMapping("/book")
    public String bookAppointment(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("appointment", new Appointment());
        return "patient/book-appointment";
    }

    @PostMapping("/book")
    public String createAppointment(@RequestParam String doctorId,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                                    @RequestParam String reason,
                                    Principal principal) {
        Patient patient = currentPatient(principal);
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        appointmentRepository.save(new Appointment(
                patient.getId(), patient.getName(), doctor.getId(), doctor.getName(),
                dateTime, reason, AppointmentStatus.PENDING));
        return "redirect:/patient/appointments?booked=true";
    }

    @GetMapping("/appointments")
    public String appointments(Model model, Principal principal) {
        Patient patient = currentPatient(principal);
        model.addAttribute("appointments", appointmentRepository.findByPatientIdOrderByDateTimeAsc(patient.getId()));
        return "patient/appointments";
    }

    @GetMapping("/records")
    public String records(Model model, Principal principal) {
        Patient patient = currentPatient(principal);
        model.addAttribute("records", recordRepository.findByPatientIdOrderByDateDesc(patient.getId()));
        return "patient/records";
    }
}
