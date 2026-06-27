package com.liedetector.liedetector;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edgar")
public class EdgarController {

    private final EdgarService edgarService;
    private final CompanyRepository companyRepository;
    private final EarningsCallRepository earningsCallRepository;
    private final TransparencyScoreRepository scoreRepository;
    private final NlpService nlpService;

    public EdgarController(EdgarService edgarService,
                           CompanyRepository companyRepository,
                           EarningsCallRepository earningsCallRepository,
                           TransparencyScoreRepository scoreRepository,
                           NlpService nlpService) {
        this.edgarService = edgarService;
        this.companyRepository = companyRepository;
        this.earningsCallRepository = earningsCallRepository;
        this.scoreRepository = scoreRepository;
        this.nlpService = nlpService;
    }

    @GetMapping("/cik/{ticker}")
    public String getCik(@PathVariable String ticker) {
        return edgarService.getCikFromTicker(ticker);
    }

    @GetMapping("/latest-filing/{ticker}")
    public String getLatestFiling(@PathVariable String ticker) {
        return edgarService.getLatestFilingAccession(ticker);
    }

    @GetMapping("/filing-text/{ticker}")
    public String getFilingText(@PathVariable String ticker) {
        return edgarService.fetch10QText(ticker);
    }

    @PostMapping("/analyze/{ticker}")
    public TransparencyScore analyze(@PathVariable String ticker) {
        return edgarService.analyzeCompany(ticker, companyRepository,
                earningsCallRepository, scoreRepository, nlpService);
    }
}