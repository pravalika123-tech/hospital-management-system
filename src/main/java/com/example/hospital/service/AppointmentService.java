package com.example.hospital.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.example.hospital.exception.ConflictException;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.repository.AppointmentRepository;

@Service
public class AppointmentService {
    private static final int BUFFER_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment book(String patientId, String patientName, String doctorId, String doctorName,
                            LocalDateTime dateTime, String reason) {
        LocalDateTime windowStart = dateTime.minusMinutes(BUFFER_MINUTES);
        LocalDateTime windowEnd = dateTime.plusMinutes(BUFFER_MINUTES);

        boolean doctorBusy = appointmentRepository.findByDoctorIdOrderByDateTimeAsc(doctorId).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> isOverlapping(a.getDateTime(), windowStart, windowEnd));
        if (doctorBusy) {
            throw new ConflictException("Doctor already has an appointment within "
                    + BUFFER_MINUTES + " minutes of the requested time.");
        }

        boolean patientBusy = appointmentRepository.findByPatientIdOrderByDateTimeAsc(patientId).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> isOverlapping(a.getDateTime(), windowStart, windowEnd));
        if (patientBusy) {
            throw new ConflictException("You already have an appointment within "
                    + BUFFER_MINUTES + " minutes of the requested time.");
        }

        return appointmentRepository.save(new Appointment(
                patientId, patientName, doctorId, doctorName, dateTime, reason, AppointmentStatus.PENDING));
    }

    private boolean isOverlapping(LocalDateTime existing, LocalDateTime start, LocalDateTime end) {
        return existing.isAfter(start) && existing.isBefore(end);
    }
}
