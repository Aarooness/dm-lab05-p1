package com.aarooness.champions.dataaccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidos")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_externo", nullable = false, unique = true, length = 100)
    private String idExterno;

    @Column(name = "equipo_local", nullable = false, length = 120)
    private String equipoLocal;

    @Column(name = "equipo_visitante", nullable = false, length = 120)
    private String equipoVisitante;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 100)
    private String fase;

    protected Match() {
    }

    public Match(String idExterno, String equipoLocal, String equipoVisitante, LocalDateTime fecha, String fase) {
        this.idExterno = idExterno;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fecha = fecha;
        this.fase = fase;
    }

    public Long getId() {
        return id;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFase() {
        return fase;
    }

    public void updateFrom(MatchData data, LocalDateTime matchDate) {
        equipoLocal = data.equipoLocal().trim();
        equipoVisitante = data.equipoVisitante().trim();
        fecha = matchDate;
        fase = data.fase().trim();
    }
}
