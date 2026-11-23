package com.example.hospital.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private int experienceYears;
    private double consultationFee;
    private boolean available;

    public Doctor() {
    }

    public Doctor(String name, String specialization, String email, String phone,
                  int experienceYears, double consultationFee, boolean available) {
        this.name = name;
        this.specialization = specialization;
        this.email = email;
        this.phone = phone;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.available = available;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
