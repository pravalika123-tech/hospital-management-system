package com.example.hospital.controller;

import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.MedicalRecord;
import com.example.hospital.model.Patient;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;

    public DoctorController(UserRepository userRepository, DoctorRepository doctorRepository,
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
                .orElseThrow(() -> new IllegalStateException("Signed-in user not found"));
        return doctorRepository.findById(user.getLinkedId())
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointmentRepository.findByDoctorIdOrderByDateTimeAsc(doctor.getId()));
        model.addAttribute("records", recordRepository.findByDoctorIdOrderByDateDesc(doctor.getId()));
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Model model, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        model.addAttribute("appointments", appointmentRepository.findByDoctorIdOrderByDateTimeAsc(doctor.getId()));
        model.addAttribute("statuses", AppointmentStatus.values());
        return "doctor/appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam AppointmentStatus status,
                               Principal principal) {
        Doctor doctor = currentDoctor(principal);
        appointmentRepository.findById(id).ifPresent(appointment -> {
            if (doctor.getId().equals(appointment.getDoctorId())) {
                appointment.setStatus(status);
                appointmentRepository.save(appointment);
            }
        });
        return "redirect:/doctor/appointments";
    }

    @GetMapping("/records")
    public String records(Model model, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        model.addAttribute("records", recordRepository.findByDoctorIdOrderByDateDesc(doctor.getId()));
        return "doctor/records";
    }

    @GetMapping("/records/new")
    public String newRecord(@RequestParam(required = false) String patientId,
                            Model model, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("selectedPatientId", patientId);
        model.addAttribute("record", new MedicalRecord());
        return "doctor/record-form";
    }

    @PostMapping("/records")
    public String saveRecord(@ModelAttribute MedicalRecord record, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        record.setDoctorId(doctor.getId());
        record.setDoctorName(doctor.getName());
        record.setDate(LocalDate.now());
        patientRepository.findById(record.getPatientId()).ifPresent(p -> record.setPatientName(p.getName()));
        recordRepository.save(record);
        return "redirect:/doctor/records";
    }
}
