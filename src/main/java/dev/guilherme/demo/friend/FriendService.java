package dev.guilherme.demo.friend;

import dev.guilherme.demo.friend.dtos.FriendRequestDTO;
import dev.guilherme.demo.friend.dtos.FriendResponseDTO;
import dev.guilherme.demo.friend.dtos.RankingDTO;
import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;

    private final UserService userService;

    private final StudySessionRepository studySessionRepository;

    public FriendResponseDTO sendRequest(FriendRequestDTO dto) {
        UserModel user = getCurrentUser();
        UserModel friend = userService.findById(dto.friendId());


        if (user.getId().equals(friend.getId()) ||
                friendRepository.findByUserIdAndFriendId(user.getId(), friend.getId()).isPresent()) {
            throw new RuntimeException("Amizade inválida");
        }

        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status(Friendship.Status.PENDING)
                .build();

        Friendship saved = friendRepository.save(friendship);
        return toResponse(saved);
    }

    public List<FriendResponseDTO> acceptRequest(Long requestId) {
        UserModel user = getCurrentUser();
        Friendship friendship = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!friendship.getFriend().getId().equals(user.getId())) {
            throw new RuntimeException("Apenas destinatário pode aceitar");
        }

        friendship.setStatus(Friendship.Status.ACCEPTED);
        friendRepository.save(friendship);

        return getFriends();
    }

    public List<FriendResponseDTO> getFriends() {
        UserModel user = getCurrentUser();
        List<Friendship> friendships = friendRepository.findByUserIdOrFriendId(user.getId(), user.getId());

        return friendships.stream()
                .filter(f -> f.getStatus() == Friendship.Status.ACCEPTED)
                .map(this::toResponseWithStats)
                .collect(Collectors.toList());
    }

    public List<RankingDTO> getWeeklyRanking() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
        return friendRepository.getWeeklyRanking(startOfWeek);
    }

    private FriendResponseDTO toResponseWithStats(Friendship friendship) {
        UserModel friend = friendship.getFriend().equals(getCurrentUser()) ?
                friendship.getUser() : friendship.getFriend();

        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        Integer weeklyMinutes = studySessionRepository.getWeeklyMinutes(friend.getId(), startOfWeek, endOfWeek);
        Double weeklyHours = (weeklyMinutes != null) ? weeklyMinutes / 60.0 : 0.0;
        Integer position = getRankingPosition(friend.getId());

        return new FriendResponseDTO(
                friendship.getId(),
                friend.getId(),
                friend.getName(),
                friend.getEmail(),
                weeklyHours,
                position,
                friendship.getCreatedAt()
        );
    }

    private FriendResponseDTO toResponse(Friendship friendship) {
        UserModel friend = friendship.getFriend().equals(getCurrentUser()) ?
                friendship.getUser() : friendship.getFriend();

        return new FriendResponseDTO(
                friendship.getId(),
                friend.getId(),
                friend.getName(),
                friend.getEmail(),
                0.0,
                null,
                friendship.getCreatedAt()
        );
    }

    private Integer getRankingPosition(Long userId) {
        return (int) (Math.random() * 10 + 1);
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}