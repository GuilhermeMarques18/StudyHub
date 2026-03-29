package dev.guilherme.demo.study.exception;

public class TopicSubjectMismatchException extends RuntimeException {
    public TopicSubjectMismatchException(String message) {
        super(message);
    }
}
