package com.liedetector.liedetector;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edgar")
public class EdgarController {

    private final EdgarService edgarService;

    public EdgarController(EdgarService edgarService) {
        this.edgarService = edgarService;
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
}