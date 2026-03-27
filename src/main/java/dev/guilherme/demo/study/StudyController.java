package dev.guilherme.demo.study;

import dev.guilherme.demo.study.dtos.StudySessionDTO;
import dev.guilherme.demo.study.dtos.StudySessionResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/sessions")
    public ResponseEntity<StudySessionResponseDTO> saveSession(
            @Valid @RequestBody StudySessionDTO dto) {
        return ResponseEntity.ok(studyService.createSession(dto));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<StudySessionResponseDTO>> getMySessions() {
        return ResponseEntity.ok(studyService.getSessionsByUser());
    }

    @GetMapping("/sessions/subject/{subjectId}")
    public ResponseEntity<List<StudySessionResponseDTO>> getSessionsBySubject(
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(studyService.getSessionsBySubject(subjectId));
    }
}