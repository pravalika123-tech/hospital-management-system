package com.example.hospital.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.hospital.model.Patient;
import com.example.hospital.model.Role;
import com.example.hospital.model.User;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PatientRepository patientRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean doctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        if (admin) return "redirect:/admin/dashboard";
        if (doctor) return "redirect:/doctor/dashboard";
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("patient", new Patient());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient, Model model) {
        if (patient.getUsername() == null || patient.getUsername().isBlank()) {
            model.addAttribute("error", "Username is required.");
            return "register";
        }
        if (userRepository.existsByUsername(patient.getUsername())) {
            model.addAttribute("error", "That username is already taken.");
            return "register";
        }
        Patient saved = patientRepository.save(patient);
        userRepository.save(new User(patient.getUsername(), passwordEncoder.encode(patient.getPhone()),
                patient.getEmail(), Role.PATIENT, true, saved.getId()));
        return "redirect:/login?registered=true";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
