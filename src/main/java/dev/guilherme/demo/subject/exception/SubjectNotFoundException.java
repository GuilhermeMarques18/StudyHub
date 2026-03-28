package dev.guilherme.demo.subject.exception;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(Long id) {
        super("Matéria com id " + id + " não encontrada");
    }
}