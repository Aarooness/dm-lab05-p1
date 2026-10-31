package com.aarooness.champions.presentation;

import com.aarooness.champions.business.MatchService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("app")
public class PredictionController {
    private final MatchService matchService;

    public PredictionController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/predicciones")
    public String predictions(Model model) {
        model.addAttribute("predicciones", matchService.getPredictions());
        return "predicciones";
    }
}
