package com.aarooness.champions.business;

import com.aarooness.champions.dataaccess.Match;
import com.aarooness.champions.dataaccess.MatchData;
import com.aarooness.champions.dataaccess.MatchRepository;
import com.aarooness.champions.dataaccess.Prediction;
import com.aarooness.champions.dataaccess.PredictionRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("app")
public class MatchService {
    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;

    public MatchService(MatchRepository matchRepository, PredictionRepository predictionRepository) {
        this.matchRepository = matchRepository;
        this.predictionRepository = predictionRepository;
    }

    @Transactional
    public int storeMatches(List<MatchData> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Falló la importación inicial: la API no devolvió partidos.");
        }

        Set<String> externalIds = new HashSet<>();
        Map<String, LocalDateTime> matchDates = new HashMap<>();
        for (MatchData match : data) {
            validateMatch(match, externalIds, matchDates);
        }

        Map<String, Match> existing = new HashMap<>();
        matchRepository.findAllByIdExternoIn(externalIds)
                .forEach(match -> existing.put(match.getIdExterno(), match));

        for (MatchData match : data) {
            String externalId = match.idExterno().trim();
            LocalDateTime date = matchDates.get(externalId);
            Match stored = existing.get(externalId);
            if (stored == null) {
                existing.put(externalId, new Match(externalId, match.equipoLocal().trim(),
                        match.equipoVisitante().trim(), date, match.fase().trim()));
            } else {
                stored.updateFrom(match, date);
            }
        }
        matchRepository.saveAll(existing.values());
        return data.size();
    }

    @Transactional(readOnly = true)
    public List<Match> getMatches() {
        return matchRepository.findAllByOrderByFechaAsc();
    }

    @Transactional(readOnly = true)
    public Match getMatch(Long id) {
        return matchRepository.findById(id).orElseThrow(() -> new MatchNotFoundException(id));
    }

    @Transactional
    public Prediction createPrediction(Long matchId, PredictionCommand command) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        if (command == null || isBlank(command.golesLocal()) || isBlank(command.golesVisitante())
                || isBlank(command.resultado())) {
            throw new BusinessValidationException("Completa el resultado y los goles de ambos equipos.");
        }

        int homeGoals = parseGoals(command.golesLocal(), "local");
        int awayGoals = parseGoals(command.golesVisitante(), "visitante");
        String selectedResult = command.resultado().trim().toUpperCase(Locale.ROOT);
        String expectedResult = homeGoals > awayGoals ? "LOCAL"
                : homeGoals < awayGoals ? "VISITANTE" : "EMPATE";
        if (!Set.of("LOCAL", "VISITANTE", "EMPATE").contains(selectedResult)) {
            throw new BusinessValidationException("Selecciona local, visitante o empate.");
        }
        if (!selectedResult.equals(expectedResult)) {
            throw new BusinessValidationException(
                    "El resultado elegido no coincide con el marcador estimado (" + expectedResult.toLowerCase()
                            + ").");
        }

        return predictionRepository.save(new Prediction(match, selectedResult, homeGoals, awayGoals,
                LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<Prediction> getPredictions() {
        return predictionRepository.findAllByOrderByFechaRegistroDesc();
    }

    private void validateMatch(MatchData match, Set<String> externalIds,
                               Map<String, LocalDateTime> matchDates) {
        if (match == null || isBlank(match.idExterno()) || isBlank(match.equipoLocal())
                || isBlank(match.equipoVisitante()) || isBlank(match.fecha()) || isBlank(match.fase())) {
            throw new IllegalStateException("La API devolvió un partido incompleto.");
        }
        String externalId = match.idExterno().trim();
        if (!externalIds.add(externalId)) {
            throw new IllegalStateException("La API devolvió un ID externo duplicado: " + externalId + ".");
        }
        try {
            matchDates.put(externalId, LocalDateTime.parse(match.fecha().trim()));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("La fecha del partido " + externalId + " no tiene formato válido.",
                    exception);
        }
    }

    private int parseGoals(String value, String team) {
        try {
            int goals = Integer.parseInt(value.trim());
            if (goals < 0) {
                throw new BusinessValidationException("Los goles del equipo " + team + " no pueden ser negativos.");
            }
            return goals;
        } catch (NumberFormatException exception) {
            throw new BusinessValidationException(
                    "Los goles del equipo " + team + " deben ser un número entero no negativo.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
