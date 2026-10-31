package com.aarooness.champions.business;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("app")
public class DatabaseHealthService {
    private final DataSource dataSource;

    public DatabaseHealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isDatabaseAvailable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        }
    }
}
