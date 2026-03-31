package dev.guilherme.demo.group;

import dev.guilherme.demo.group.dtos.GroupDTO;
import dev.guilherme.demo.group.dtos.GroupJoinDTO;
import dev.guilherme.demo.group.dtos.GroupResponseDTO;
import dev.guilherme.demo.group.dtos.MemberResponseDTO;
import dev.guilherme.demo.group.exception.GroupAccessDeniedException;
import dev.guilherme.demo.group.exception.InvalidGroupActionException;
import dev.guilherme.demo.group.exception.ResourceNotFoundException;
import dev.guilherme.demo.study.group.StudyGroupModel;
import dev.guilherme.demo.study.group.StudyGroupRepository;
import dev.guilherme.demo.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return groupRepository.findByIsPrivateFalse()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GroupResponseDTO joinGroup(GroupJoinDTO dto) {
        UserModel user = getCurrentUser();

        StudyGroupModel group = groupRepository.findById(dto.groupId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado"));

        if (memberRepository.findByGroupIdAndUserId(group.getId(), user.getId()).isPresent()) {
            throw new InvalidGroupActionException("Você já pertence a esse grupo");
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
            throw new GroupAccessDeniedException("Acesso negado: você não pertence a este grupo");
        }

        return memberRepository.findByGroupIdOrderByJoinedDate(groupId)
                .stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    public MemberResponseDTO promoteToAdmin(Long groupId, Long memberId) {
        UserModel user = getCurrentUser();

        GroupMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!member.getGroup().getId().equals(groupId)) {
            throw new InvalidGroupActionException("O membro não pertence a este grupo");
        }

        if (member.getRole() == GroupMember.Role.ADMIN || !isGroupAdmin(groupId, user.getId())) {
            throw new GroupAccessDeniedException("Permissão negada ou o usuário já é um administrador");
        }

        member.setRole(GroupMember.Role.ADMIN);
        return toMemberResponse(memberRepository.save(member));
    }

    public void leaveGroup(Long groupId) {
        UserModel user = getCurrentUser();

        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Você não é membro deste grupo"));

        memberRepository.delete(member);
    }

    public void removeMember(Long groupId, Long memberId) {
        UserModel user = getCurrentUser();

        if (!isGroupAdmin(groupId, user.getId())) {
            throw new GroupAccessDeniedException("Acesso negado: apenas administradores podem remover membros");
        }

        GroupMember memberToRemove = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!memberToRemove.getGroup().getId().equals(groupId)) {
            throw new InvalidGroupActionException("O membro não pertence a este grupo");
        }

        memberRepository.delete(memberToRemove);
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
        Long memberCount = memberRepository.countByGroupId(group.getId());
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