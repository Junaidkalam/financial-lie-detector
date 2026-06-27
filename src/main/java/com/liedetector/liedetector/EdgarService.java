package com.liedetector.liedetector;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class EdgarService {

    private final RestTemplate restTemplate;
    private final CompanyRepository companyRepository;
    private final EarningsCallRepository earningsCallRepository;

    public EdgarService(CompanyRepository companyRepository,
                        EarningsCallRepository earningsCallRepository) {
        this.companyRepository = companyRepository;
        this.earningsCallRepository = earningsCallRepository;

        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "FinancialLieDetector junaidkalam@example.com");
            request.getHeaders().set("Accept", "application/json");
            return execution.execute(request, body);
        });
    }

    public String getCikFromTicker(String ticker) {
        String url = "https://www.sec.gov/files/company_tickers.json";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        for (Object value : response.values()) {
            Map<String, Object> entry = (Map<String, Object>) value;
            if (ticker.equalsIgnoreCase((String) entry.get("ticker"))) {
                int cik = (int) entry.get("cik_str");
                return String.format("%010d", cik);
            }
        }
        throw new RuntimeException("CIK not found for ticker: " + ticker);
    }

    public Map<String, Object> getFilings(String cik) {
        String url = "https://data.sec.gov/submissions/CIK" + cik + ".json";
        return restTemplate.getForObject(url, Map.class);
    }

    public String getLatestFilingAccession(String ticker) {
        String cik = getCikFromTicker(ticker);
        Map<String, Object> filings = getFilings(cik);

        Map<String, Object> filingsObj = (Map<String, Object>) filings.get("filings");
        Map<String, Object> recent = (Map<String, Object>) filingsObj.get("recent");
        List<String> forms = (List<String>) recent.get("form");
        List<String> accessions = (List<String>) recent.get("accessionNumber");
        List<String> dates = (List<String>) recent.get("filingDate");
        List<String> primaryDocs = (List<String>) recent.get("primaryDocument");

        for (int i = 0; i < forms.size(); i++) {
            if ("10-Q".equals(forms.get(i))) {
                return accessions.get(i).replace("-", "") + "|" + dates.get(i) + "|" + primaryDocs.get(i);
            }
        }
        throw new RuntimeException("No 10-Q found for ticker: " + ticker);
    }

    public String fetch10QText(String ticker) {
        String result = getLatestFilingAccession(ticker);
        String[] parts = result.split("\\|");
        String accession = parts[0];
        String primaryDoc = parts[2];
        String cik = getCikFromTicker(ticker);
        String cikStripped = cik.replaceFirst("^0+", "");

        String docUrl = "https://www.sec.gov/Archives/edgar/data/" +
                cikStripped + "/" + accession + "/" + primaryDoc;

        try {
            return restTemplate.getForObject(docUrl, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch 10-Q text: " + e.getMessage());
        }
    }
    public TransparencyScore analyzeCompany(String ticker, CompanyRepository companyRepository,
                                            EarningsCallRepository earningsCallRepository,
                                            TransparencyScoreRepository scoreRepository,
                                            NlpService nlpService) {
        // Step 1: Get or create company
        Company company = companyRepository.findAll().stream()
                .filter(c -> c.getTicker().equalsIgnoreCase(ticker))
                .findFirst()
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setTicker(ticker.toUpperCase());
                    newCompany.setName(ticker.toUpperCase());
                    return companyRepository.save(newCompany);
                });

        // Step 2: Fetch 10-Q text and filing info
        String result = getLatestFilingAccession(ticker);
        String[] parts = result.split("\\|");
        String filingDate = parts[1];
        String rawText = fetch10QText(ticker);

        // Step 3: Strip HTML tags to get plain text
        // Remove script and style blocks entirely
        // Step 3: Strip HTML tags to get plain text
        String plainText = rawText.replaceAll("(?s)<script[^>]*>.*?</script>", " ");
        plainText = plainText.replaceAll("(?s)<style[^>]*>.*?</style>", " ");
        plainText = plainText.replaceAll("<[^>]*>", " ");
        plainText = plainText.replaceAll("\\b\\w+:\\w+\\b", " ");
        plainText = plainText.replaceAll("\\b\\d{10}\\b", " ");
        plainText = plainText.replaceAll("\\s+", " ").trim();

        // Skip past XBRL metadata - find where real text starts
        int startIndex = plainText.indexOf("UNITED STATES");
        if (startIndex == -1) startIndex = plainText.indexOf("Apple");
        if (startIndex == -1) startIndex = 0;

        plainText = plainText.substring(startIndex);
        if (plainText.length() > 5000) {
            plainText = plainText.substring(0, 5000);
        }

        // Step 4: Save as EarningsCall
        EarningsCall call = new EarningsCall();
        call.setCompany(company);
        call.setQuarter("Q1");
        call.setYear(2026);
        call.setCallDate(java.time.LocalDate.parse(filingDate));
        call.setTranscriptText(plainText);
        earningsCallRepository.save(call);

        // Step 5: Run NLP analysis
        var nlpResult = nlpService.analyze(plainText);
        TransparencyScore score = new TransparencyScore();
        score.setEarningsCall(call);
        score.setHedgeCount(((Number) nlpResult.get("hedge_count")).intValue());
        score.setPassiveCount(((Number) nlpResult.get("passive_count")).intValue());
        score.setSentimentCompound(((Number) nlpResult.get("sentiment_compound")).doubleValue());
        score.setTransparencyScore(((Number) nlpResult.get("transparency_score")).doubleValue());
        return scoreRepository.save(score);
    }
}