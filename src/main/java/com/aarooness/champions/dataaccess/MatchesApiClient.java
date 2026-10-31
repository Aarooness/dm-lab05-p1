package com.aarooness.champions.dataaccess;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("app")
public class MatchesApiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchesApiClient.class);
    private static final int MAX_ATTEMPTS = 6;

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public MatchesApiClient(RestTemplateBuilder builder,
                            @Value("${matches.api.url}") String apiUrl) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
        this.apiUrl = apiUrl;
    }

    public List<MatchData> fetchMatches() {
        RestClientException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                MatchData[] response = restTemplate.getForObject(apiUrl, MatchData[].class);
                if (response == null) {
                    throw new IllegalStateException("La API respondió sin un listado de partidos.");
                }
                return Arrays.asList(response);
            } catch (RestClientException exception) {
                lastFailure = exception;
                LOGGER.warn("No se pudo consultar la API de partidos (intento {}/{}): {}",
                        attempt, MAX_ATTEMPTS, exception.getMessage());
                if (attempt < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Se interrumpió la espera de la API de partidos.",
                                interrupted);
                    }
                }
            }
        }
        throw new IllegalStateException(
                "Falló la importación inicial: no se pudo consultar " + apiUrl
                        + " después de " + MAX_ATTEMPTS + " intentos.",
                lastFailure);
    }
}
