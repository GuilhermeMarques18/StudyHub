package dev.guilherme.demo.group;


import dev.guilherme.demo.group.dtos.GroupDTO;
import dev.guilherme.demo.group.dtos.GroupJoinDTO;
import dev.guilherme.demo.group.dtos.GroupResponseDTO;
import dev.guilherme.demo.group.dtos.MemberResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    @Autowired
    private GroupService groupService;


    @PostMapping
    public ResponseEntity<GroupResponseDTO> saveGroup(@Valid @RequestBody GroupDTO dto){
        return ResponseEntity.ok(groupService.create(dto));
    }

    @PostMapping("/join")
    public ResponseEntity<GroupResponseDTO> join(@Valid @RequestBody GroupJoinDTO dto){
        return ResponseEntity.ok(groupService.joinGroup(dto));
    }
    @GetMapping("/mine")
    public ResponseEntity<List<GroupResponseDTO>> getMyGroups() {
        return ResponseEntity.ok(groupService.getMyGroups());
    }

    @GetMapping("/public")
    public ResponseEntity<List<GroupResponseDTO>> getPublicGroups() {
        return ResponseEntity.ok(groupService.getPublicGroups());
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MemberResponseDTO>> getMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    @PostMapping("/{groupId}/members/{memberId}/promote")
    public ResponseEntity<MemberResponseDTO> promoteToAdmin(
            @PathVariable Long groupId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(groupService.promoteToAdmin(groupId, memberId));
    }

}
