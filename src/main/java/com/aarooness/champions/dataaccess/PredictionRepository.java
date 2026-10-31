package com.aarooness.champions.dataaccess;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    @EntityGraph(attributePaths = "partido")
    List<Prediction> findAllByOrderByFechaRegistroDesc();
}
