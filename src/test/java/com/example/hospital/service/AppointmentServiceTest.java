package com.example.hospital.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.hospital.exception.ConflictException;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void rejectsOverlappingDoctorSlot() {
        LocalDateTime requested = LocalDateTime.of(2026, 11, 30, 10, 30);
        Appointment existing = new Appointment("p1", "Patient One", "d1", "Dr. One",
                requested, "Check-up", AppointmentStatus.APPROVED);
        when(appointmentRepository.findByDoctorIdOrderByDateTimeAsc("d1")).thenReturn(List.of(existing));

        assertThrows(ConflictException.class, () -> appointmentService.book(
                "p2", "Patient Two", "d1", "Dr. One", requested.plusMinutes(10), "Follow-up"));
    }

    @Test
    void booksFreeSlot() {
        LocalDateTime requested = LocalDateTime.of(2026, 11, 30, 10, 30);
        when(appointmentRepository.findByDoctorIdOrderByDateTimeAsc("d1")).thenReturn(List.of());
        when(appointmentRepository.findByPatientIdOrderByDateTimeAsc("p2")).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment booked = appointmentService.book(
                "p2", "Patient Two", "d1", "Dr. One", requested, "Follow-up");
        assertNotNull(booked);
        assertNotNull(booked.getCreatedAt());
    }
}
