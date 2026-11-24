package com.example.hospital.dto;

import jakarta.validation.constraints.NotBlank;

public record MedicalRecordRequest(
        @NotBlank(message = "Patient id is required") String patientId,
        @NotBlank(message = "Diagnosis is required") String diagnosis,
        String prescription,
        String notes) {
}
