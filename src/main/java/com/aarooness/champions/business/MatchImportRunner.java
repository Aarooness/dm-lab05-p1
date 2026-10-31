package com.aarooness.champions.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("app")
@ConditionalOnProperty(name = "matches.import-on-startup", havingValue = "true", matchIfMissing = true)
public class MatchImportRunner implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchImportRunner.class);
    private final MatchImportService matchImportService;

    public MatchImportRunner(MatchImportService matchImportService) {
        this.matchImportService = matchImportService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int count = matchImportService.importMatches();
            LOGGER.info("Importación inicial finalizada: {} partidos recibidos y guardados.", count);
        } catch (RuntimeException exception) {
            LOGGER.error("La importación inicial de partidos falló. La aplicación no se iniciará correctamente.",
                    exception);
            throw exception;
        }
    }
}
