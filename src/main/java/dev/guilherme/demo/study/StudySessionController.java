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
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping("/sessions")
    public ResponseEntity<StudySessionResponseDTO> saveSession(
            @Valid @RequestBody StudySessionDTO dto) {
        return ResponseEntity.ok(studySessionService.createSession(dto));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<StudySessionResponseDTO>> getMySessions() {
        return ResponseEntity.ok(studySessionService.getSessionsByUser());
    }

    @GetMapping("/sessions/subject/{subjectId}")
    public ResponseEntity<List<StudySessionResponseDTO>> getSessionsBySubject(
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(studySessionService.getSessionsBySubject(subjectId));
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<StudySessionResponseDTO> updateSession(@PathVariable Long id, @Valid @RequestBody StudySessionDTO dto) {
        return ResponseEntity.ok(studySessionService.updateSession(id, dto));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        studySessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}