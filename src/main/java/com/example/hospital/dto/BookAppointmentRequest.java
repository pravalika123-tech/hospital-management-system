package com.example.hospital.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookAppointmentRequest(
        @NotBlank(message = "Doctor id is required") String doctorId,
        @NotNull(message = "Date and time is required") @Future(message = "Appointment must be in the future") LocalDateTime dateTime,
        @NotBlank(message = "Reason is required") String reason) {
}
