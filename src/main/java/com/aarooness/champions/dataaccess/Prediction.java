package com.aarooness.champions.dataaccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "predicciones")
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partido_id", nullable = false)
    private Match partido;

    @Column(nullable = false, length = 20)
    private String resultado;

    @Column(name = "goles_local", nullable = false)
    private int golesLocal;

    @Column(name = "goles_visitante", nullable = false)
    private int golesVisitante;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    protected Prediction() {
    }

    public Prediction(Match partido, String resultado, int golesLocal, int golesVisitante,
                      LocalDateTime fechaRegistro) {
        this.partido = partido;
        this.resultado = resultado;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public Match getPartido() {
        return partido;
    }

    public String getResultado() {
        return resultado;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
