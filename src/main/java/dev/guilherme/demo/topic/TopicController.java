package dev.guilherme.demo.topic;

import dev.guilherme.demo.topic.dtos.TopicDTO;
import dev.guilherme.demo.topic.dtos.TopicResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponseDTO> saveTopic(@Valid @RequestBody TopicDTO dto) {
        return ResponseEntity.ok(topicService.save(dto));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<TopicResponseDTO>> getBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(topicService.getBySubject(subjectId));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TopicResponseDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.complete(id));
    }
}