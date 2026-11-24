package com.example.hospital.dto;

public record LoginResponse(String accessToken, String tokenType, String username, String role) {
}
