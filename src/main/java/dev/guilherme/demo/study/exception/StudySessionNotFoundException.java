package dev.guilherme.demo.study.exception;

public class StudySessionNotFoundException extends RuntimeException {
    public StudySessionNotFoundException(Long id) {
        super("Sessão de estudo com id " + id + " não encontrada");
    }
}
