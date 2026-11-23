package com.example.hospital.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Role;
import com.example.hospital.model.User;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, DoctorRepository doctorRepository,
                           PatientRepository patientRepository, AppointmentRepository appointmentRepository,
                           MedicalRecordRepository recordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository = recordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("doctorCount", doctorRepository.count());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("appointmentCount", appointmentRepository.count());
        model.addAttribute("recordCount", recordRepository.count());
        model.addAttribute("pending", appointmentRepository.findByStatusOrderByDateTimeAsc(AppointmentStatus.PENDING));
        model.addAttribute("approved", appointmentRepository.findByStatusOrderByDateTimeAsc(AppointmentStatus.APPROVED));
        return "admin/dashboard";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "admin/doctors";
    }

    @GetMapping("/doctors/new")
    public String doctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "admin/doctor-form";
    }

    @PostMapping("/doctors")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        String username = doctor.getEmail() == null ? "doctor" + saved.getId()
                : doctor.getEmail().split("@")[0];
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(new User(username, passwordEncoder.encode("doctor123"),
                    doctor.getEmail(), Role.DOCTOR, true, saved.getId()));
        }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable String id) {
        doctorRepository.deleteById(id);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "admin/patients";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentRepository.findAll();
        appointments.sort((a, b) -> b.getDateTime().compareTo(a.getDateTime()));
        model.addAttribute("appointments", appointments);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "admin/appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam AppointmentStatus status) {
        appointmentRepository.findById(id).ifPresent(appointment -> {
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        });
        return "redirect:/admin/appointments";
    }
}
