package com.aarooness.champions.presentation;

import com.aarooness.champions.business.BusinessValidationException;
import com.aarooness.champions.business.MatchNotFoundException;
import com.aarooness.champions.business.MatchService;
import com.aarooness.champions.business.PredictionCommand;
import com.aarooness.champions.dataaccess.Match;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Profile("app")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String matches(Model model) {
        model.addAttribute("partidos", matchService.getMatches());
        return "partidos";
    }

    @GetMapping("/partidos/{id}/prediccion")
    public String predictionForm(@PathVariable Long id, Model model) {
        addPredictionFormModel(id, model, new PredictionForm());
        return "prediccion";
    }

    @PostMapping("/partidos/{id}/prediccion")
    public String createPrediction(@PathVariable Long id, @ModelAttribute PredictionForm form, Model model) {
        try {
            matchService.createPrediction(id,
                    new PredictionCommand(form.getResultado(), form.getGolesLocal(), form.getGolesVisitante()));
            return "redirect:/predicciones";
        } catch (BusinessValidationException exception) {
            addPredictionFormModel(id, model, form);
            model.addAttribute("error", exception.getMessage());
            return "prediccion";
        }
    }

    private void addPredictionFormModel(Long id, Model model, PredictionForm form) {
        Match match = matchService.getMatch(id);
        model.addAttribute("partido", match);
        model.addAttribute("form", form);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MatchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String matchNotFound(MatchNotFoundException exception, Model model) {
        model.addAttribute("mensaje", exception.getMessage());
        return "no-encontrado";
    }
}
