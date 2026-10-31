package com.aarooness.champions.dataaccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("mock")
public class MockMatchesController {
    private final List<MatchData> matches;

    public MockMatchesController(ObjectMapper objectMapper) {
        try {
            this.matches = objectMapper.readValue(
                    new ClassPathResource("mock-api/partidos.json").getInputStream(),
                    new TypeReference<>() { }
            );
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar el JSON de partidos simulados.", exception);
        }
    }

    @GetMapping("/api/partidos")
    public List<MatchData> getMatches() {
        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "El catálogo simulado de partidos está vacío.");
        }
        return matches;
    }
}
