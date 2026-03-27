package dev.guilherme.demo.group;
import dev.guilherme.demo.group.dtos.GroupDTO;
import dev.guilherme.demo.group.dtos.GroupJoinDTO;
import dev.guilherme.demo.group.dtos.GroupResponseDTO;
import dev.guilherme.demo.group.dtos.MemberResponseDTO;
import dev.guilherme.demo.study.group.StudyGroupModel;
import dev.guilherme.demo.study.group.StudyGroupRepository;
import dev.guilherme.demo.user.UserModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    public GroupResponseDTO create(GroupDTO dto) {
        UserModel creator = getCurrentUser();

        StudyGroupModel group = StudyGroupModel.builder()
                .name(dto.name())
                .description(dto.description())
                .isPrivate(dto.isPrivate() != null ? dto.isPrivate() : false)
                .creator(creator)
                .build();

        StudyGroupModel saved = groupRepository.save(group);


        memberRepository.save(GroupMember.builder()
                .group(saved)
                .user(creator)
                .role(GroupMember.Role.ADMIN)
                .build());

        return toResponse(saved);
    }

    public List<GroupResponseDTO> getMyGroups() {
        UserModel user = getCurrentUser();
        return groupRepository.findByCreatorIdOrderByCreatedDateDesc(user.getId())
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<GroupResponseDTO> getPublicGroups() {
        UserModel user = getCurrentUser();
        return groupRepository.findByIsPrivateFalse()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GroupResponseDTO joinGroup(GroupJoinDTO dto) {
        UserModel user = getCurrentUser();
        StudyGroupModel group = groupRepository.findById(dto.groupId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        if (memberRepository.findByGroupIdAndUserId(group.getId(), user.getId()).isPresent()) {
            throw new RuntimeException("Já é membro");
        }

        memberRepository.save(GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupMember.Role.MEMBER)
                .build());

        return toResponse(group);
    }

    public List<MemberResponseDTO> getGroupMembers(Long groupId) {
        UserModel user = getCurrentUser();

        if (memberRepository.findByGroupIdAndUserId(groupId, user.getId()).isEmpty()) {
            throw new RuntimeException("Acesso negado ao grupo");
        }

        return memberRepository.findByGroupIdOrderByJoinedDate(groupId)
                .stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public MemberResponseDTO promoteToAdmin(Long groupId, Long memberId) {
        UserModel user = getCurrentUser();
        GroupMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));


        if (member.getRole() == GroupMember.Role.ADMIN ||
                !isGroupAdmin(groupId, user.getId())) {
            throw new RuntimeException("Permissão negada");
        }

        member.setRole(GroupMember.Role.ADMIN);
        return toMemberResponse(memberRepository.save(member));
    }

    private boolean isGroupAdmin(Long groupId, Long userId) {
        return memberRepository.findByGroupIdAndUserId(groupId, userId)
                .map(m -> m.getRole() == GroupMember.Role.ADMIN)
                .orElse(false);
    }

    private MemberResponseDTO toMemberResponse(GroupMember member) {
        return new MemberResponseDTO(
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole().name(),
                member.getJoinedDate()
        );
    }

    private GroupResponseDTO toResponse(StudyGroupModel group) {
        Long memberCount = Long.valueOf(memberRepository.findByGroupId(group.getId()).size());
        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getIsPrivate(),
                memberCount,
                group.getCreator().getName()
        );
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
