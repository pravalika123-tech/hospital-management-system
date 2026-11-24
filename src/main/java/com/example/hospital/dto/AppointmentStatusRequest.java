package com.example.hospital.dto;

import jakarta.validation.constraints.NotNull;
import com.example.hospital.model.AppointmentStatus;

public record AppointmentStatusRequest(
        @NotNull(message = "Status is required") AppointmentStatus status) {
}
