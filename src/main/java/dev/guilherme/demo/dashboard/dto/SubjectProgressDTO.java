package dev.guilherme.demo.dashboard.dto;

public record SubjectProgressDTO( Long id,
                                  String name,
                                  String color,
                                  Double hoursStudied,
                                  Double targetHours,
                                  Double progress) {
}
