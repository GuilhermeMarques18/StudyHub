package dev.guilherme.demo.subject;

import dev.guilherme.demo.subject.dtos.SubjectDTO;
import dev.guilherme.demo.subject.dtos.SubjectResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponseDTO> saveSubject(@Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDTO>> getAll() {
        return ResponseEntity.ok(subjectService.getAll());
    }
}