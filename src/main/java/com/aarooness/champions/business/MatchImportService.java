package com.aarooness.champions.business;

import com.aarooness.champions.dataaccess.MatchData;
import com.aarooness.champions.dataaccess.MatchesApiClient;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("app")
public class MatchImportService {
    private final MatchesApiClient matchesApiClient;
    private final MatchService matchService;

    public MatchImportService(MatchesApiClient matchesApiClient, MatchService matchService) {
        this.matchesApiClient = matchesApiClient;
        this.matchService = matchService;
    }

    public int importMatches() {
        List<MatchData> matches = matchesApiClient.fetchMatches();
        return matchService.storeMatches(matches);
    }
}
