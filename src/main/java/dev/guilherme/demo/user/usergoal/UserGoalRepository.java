package dev.guilherme.demo.user.usergoal;

import dev.guilherme.demo.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoalModel, Long> {
    Optional<UserGoalModel> findByUser(UserModel user);
}