package dev.guilherme.demo.friend;

import dev.guilherme.demo.friend.dtos.FriendRequestDTO;
import dev.guilherme.demo.friend.dtos.FriendResponseDTO;
import dev.guilherme.demo.friend.dtos.RankingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendShipController {

    private final FriendShipService friendService;

    @PostMapping("/request")
    public ResponseEntity<FriendResponseDTO> sendRequest(@RequestBody FriendRequestDTO dto) {
        return ResponseEntity.ok(friendService.sendRequest(dto));
    }

    @PutMapping("/request/{requestId}/accept")
    public ResponseEntity<List<FriendResponseDTO>> acceptRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.acceptRequest(requestId));
    }

    @GetMapping
    public ResponseEntity<List<FriendResponseDTO>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @GetMapping("/ranking/weekly")
    public ResponseEntity<List<RankingDTO>> getRanking() {
        return ResponseEntity.ok(friendService.getWeeklyRanking());
    }

    @DeleteMapping("/request/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId) {
        friendService.rejectRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long friendshipId) {
        friendService.removeFriend(friendshipId);
        return ResponseEntity.noContent().build();
    }
}