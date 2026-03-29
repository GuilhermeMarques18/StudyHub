package dev.guilherme.demo.friend;

import dev.guilherme.demo.friend.dtos.FriendRequestDTO;
import dev.guilherme.demo.friend.dtos.FriendResponseDTO;
import dev.guilherme.demo.friend.dtos.RankingDTO;
import dev.guilherme.demo.friend.exception.FriendShipException;
import dev.guilherme.demo.friend.exception.FriendshipNotFoundException;
import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
            throw new FriendShipException("Amizade inválida");
        }

        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status(Friendship.Status.PENDING)
                .build();

        Friendship saved = friendRepository.save(friendship);
        return toResponse(saved, user);
    }

    public List<FriendResponseDTO> acceptRequest(Long requestId) {
        UserModel user = getCurrentUser();
        Friendship friendship = findFriendshipById(requestId);

        if (!friendship.getFriend().getId().equals(user.getId())) {
            throw new AccessDeniedException("Apenas o destinatário pode aceitar");
        }

        friendship.setStatus(Friendship.Status.ACCEPTED);
        friendRepository.save(friendship);

        return getFriends();
    }

    public void rejectRequest(Long requestId) {
        UserModel user = getCurrentUser();
        Friendship friendship = findFriendshipById(requestId);

        if (!friendship.getFriend().getId().equals(user.getId())) {
            throw new AccessDeniedException("Apenas o destinatário pode rejeitar");
        }

        friendRepository.delete(friendship);
    }

    public void removeFriend(Long friendshipId) {
        UserModel user = getCurrentUser();
        Friendship friendship = findFriendshipById(friendshipId);

        if (!friendship.getUser().getId().equals(user.getId()) && !friendship.getFriend().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        friendRepository.delete(friendship);
    }

    public List<FriendResponseDTO> getFriends() {
        UserModel user = getCurrentUser();
        List<Friendship> friendships = friendRepository.findByUserIdOrFriendId(user.getId(), user.getId());

        return friendships.stream()
                .filter(f -> f.getStatus() == Friendship.Status.ACCEPTED)
                .map(f -> toResponseWithStats(f, user))
                .collect(Collectors.toList());
    }

    private Friendship findFriendshipById(Long id) {
        return friendRepository.findById(id)
                .orElseThrow(() -> new FriendshipNotFoundException(id));
    }

    public List<RankingDTO> getWeeklyRanking() {
        return friendRepository.getWeeklyRanking(getStartOfWeek());
    }

    private LocalDateTime getStartOfWeek() {
        return LocalDateTime.now()
                .minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private FriendResponseDTO toResponseWithStats(Friendship friendship, UserModel currentUser) {
        UserModel friend = friendship.getFriend().equals(currentUser) ?
                friendship.getUser() : friendship.getFriend();

        LocalDateTime startOfWeek = getStartOfWeek();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        Integer weeklyMinutes = studySessionRepository.getWeeklyMinutes(friend.getId(), startOfWeek, endOfWeek);
        Double weeklyHours = (weeklyMinutes != null) ? weeklyMinutes / 60.0 : 0.0;

        return new FriendResponseDTO(
                friendship.getId(),
                friend.getId(),
                friend.getName(),
                friend.getEmail(),
                weeklyHours,
                friendship.getCreatedAt()
        );
    }

    private FriendResponseDTO toResponse(Friendship friendship, UserModel currentUser) {
        UserModel friend = friendship.getFriend().equals(currentUser) ?
                friendship.getUser() : friendship.getFriend();

        return new FriendResponseDTO(
                friendship.getId(),
                friend.getId(),
                friend.getName(),
                friend.getEmail(),
                0.0,
                friendship.getCreatedAt()
        );
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}