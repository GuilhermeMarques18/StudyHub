package dev.guilherme.demo.ai;

import dev.guilherme.demo.ai.dto.PromptRequestDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/askGemini")
    public String askGeminiAPI(@RequestBody PromptRequestDTO dto){
        return geminiService.askGemini(dto.prompt());
    }


}
