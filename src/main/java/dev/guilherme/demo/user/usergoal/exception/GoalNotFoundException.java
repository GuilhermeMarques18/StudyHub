package dev.guilherme.demo.user.usergoal.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException() {
        super("Meta não encontrada");
    }
}