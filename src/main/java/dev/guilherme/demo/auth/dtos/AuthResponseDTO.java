package dev.guilherme.demo.auth.dtos;

public record AuthResponseDTO(String token, String refreshToken, String tokenType) { }
