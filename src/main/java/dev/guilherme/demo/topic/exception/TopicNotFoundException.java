package dev.guilherme.demo.topic.exception;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(Long id) {
        super("Topico com id " + id + " não encontrada");
    }
}
