package com.aarooness.champions.business;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long id) {
        super("No se encontró el partido solicitado (ID " + id + ").");
    }
}
