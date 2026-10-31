package com.aarooness.champions.presentation;

import com.aarooness.champions.business.DatabaseHealthService;
import java.sql.SQLException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("app")
public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);
    private final DatabaseHealthService healthService;

    public HealthController(DatabaseHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try {
            if (healthService.isDatabaseAvailable()) {
                return ResponseEntity.ok(Map.of("estado", "correcto", "baseDeDatos", "conectada"));
            }
        } catch (SQLException exception) {
            LOGGER.warn("No se pudo comprobar la conexión a PostgreSQL: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("estado", "error", "baseDeDatos", "no disponible"));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("estado", "error", "baseDeDatos", "no disponible"));
    }
}
