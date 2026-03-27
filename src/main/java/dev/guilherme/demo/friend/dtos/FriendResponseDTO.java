package  dev.guilherme.demo.friend.dtos;

import java.time.LocalDateTime;

public record FriendResponseDTO(
        Long id,
        Long friendId,
        String friendName,
        String friendEmail,
        Double weeklyHours,
        Integer position,
        LocalDateTime createdAt
) {}