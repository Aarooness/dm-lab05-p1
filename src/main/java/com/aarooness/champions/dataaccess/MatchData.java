package com.aarooness.champions.dataaccess;

public record MatchData(
        String idExterno,
        String equipoLocal,
        String equipoVisitante,
        String fecha,
        String fase
) {
}
