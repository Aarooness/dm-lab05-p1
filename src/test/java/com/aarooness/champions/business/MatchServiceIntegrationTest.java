package com.aarooness.champions.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aarooness.champions.dataaccess.Match;
import com.aarooness.champions.dataaccess.MatchData;
import com.aarooness.champions.dataaccess.MatchRepository;
import com.aarooness.champions.dataaccess.MatchesApiClient;
import com.aarooness.champions.dataaccess.PredictionRepository;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "matches.import-on-startup=false",
        "spring.datasource.url=jdbc:h2:mem:champions-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("app")
class MatchServiceIntegrationTest {
    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchImportService matchImportService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchesApiClient matchesApiClient;

    private List<MatchData> fixtures;

    @BeforeEach
    void setUp() throws IOException {
        predictionRepository.deleteAll();
        matchRepository.deleteAll();
        fixtures = objectMapper.readValue(
                new ClassPathResource("mock-api/partidos.json").getInputStream(),
                new TypeReference<>() { }
        );
        when(matchesApiClient.fetchMatches()).thenReturn(fixtures);
    }

    @Test
    void importsEveryMatchAndRepeatedImportsDoNotDuplicate() {
        assertThat(matchImportService.importMatches()).isEqualTo(12);
        assertThat(matchImportService.importMatches()).isEqualTo(12);

        assertThat(matchRepository.count()).isEqualTo(12);
        assertThat(matchRepository.findByIdExterno("ucl-2025-001")).isPresent();
    }

    @Test
    void savesMultipleValidPredictionOutcomesForOneMatchAndReturnsTheirMatch() {
        matchImportService.importMatches();
        Match match = matchRepository.findByIdExterno("ucl-2025-001").orElseThrow();

        matchService.createPrediction(match.getId(), new PredictionCommand("LOCAL", "2", "1"));
        matchService.createPrediction(match.getId(), new PredictionCommand("VISITANTE", "1", "2"));
        matchService.createPrediction(match.getId(), new PredictionCommand("EMPATE", "0", "0"));

        assertThat(matchService.getPredictions()).hasSize(3)
                .allSatisfy(prediction ->
                        assertThat(prediction.getPartido().getIdExterno()).isEqualTo("ucl-2025-001"));
        assertThat(matchService.getPredictions()).extracting("resultado")
                .containsExactlyInAnyOrder("LOCAL", "VISITANTE", "EMPATE");
    }

    @Test
    void rejectsMismatchedResultNegativeAndUnknownMatch() {
        matchImportService.importMatches();
        Match match = matchRepository.findByIdExterno("ucl-2025-001").orElseThrow();

        assertThatThrownBy(() -> matchService.createPrediction(
                match.getId(), new PredictionCommand("EMPATE", "2", "1")))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("no coincide");
        assertThatThrownBy(() -> matchService.createPrediction(
                match.getId(), new PredictionCommand("LOCAL", "-1", "0")))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("negativos");
        assertThatThrownBy(() -> matchService.createPrediction(
                Long.MAX_VALUE, new PredictionCommand("LOCAL", "1", "0")))
                .isInstanceOf(MatchNotFoundException.class);
        assertThat(predictionRepository.count()).isZero();
    }

    @Test
    void rejectsBlankAndNonIntegerScores() {
        matchImportService.importMatches();
        Long matchId = matchRepository.findByIdExterno("ucl-2025-001").orElseThrow().getId();

        assertThatThrownBy(() -> matchService.createPrediction(
                matchId, new PredictionCommand("LOCAL", "", "1")))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Completa");
        assertThatThrownBy(() -> matchService.createPrediction(
                matchId, new PredictionCommand("LOCAL", "1.5", "0")))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("entero");
    }
}
