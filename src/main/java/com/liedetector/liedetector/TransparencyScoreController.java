package com.liedetector.liedetector;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transparency-scores")
public class TransparencyScoreController {

    private final TransparencyScoreRepository scoreRepository;
    private final EarningsCallRepository earningsCallRepository;
    private final NlpService nlpService;

    public TransparencyScoreController(TransparencyScoreRepository scoreRepository,
                                       EarningsCallRepository earningsCallRepository,
                                       NlpService nlpService) {
        this.scoreRepository = scoreRepository;
        this.earningsCallRepository = earningsCallRepository;
        this.nlpService = nlpService;
    }

    @GetMapping
    public List<TransparencyScore> getAll() {
        return scoreRepository.findAll();
    }

    @PostMapping("/analyze/{earningsCallId}")
    public TransparencyScore analyze(@PathVariable Long earningsCallId) {
        EarningsCall call = earningsCallRepository.findById(earningsCallId)
                .orElseThrow(() -> new RuntimeException("Earnings call not found"));

        var nlpResult = nlpService.analyze(call.getTranscriptText());

        TransparencyScore score = new TransparencyScore();
        score.setEarningsCall(call);
        score.setHedgeCount(((Number) nlpResult.get("hedge_count")).intValue());
        score.setPassiveCount(((Number) nlpResult.get("passive_count")).intValue());
        score.setSentimentCompound(((Number) nlpResult.get("sentiment_compound")).doubleValue());
        score.setTransparencyScore(((Number) nlpResult.get("transparency_score")).doubleValue());

        return scoreRepository.save(score);
    }
}