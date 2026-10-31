package com.aarooness.champions.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByIdExterno(String idExterno);

    List<Match> findAllByIdExternoIn(Collection<String> idsExternos);

    List<Match> findAllByOrderByFechaAsc();
}
