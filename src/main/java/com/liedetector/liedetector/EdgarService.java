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

        for (int i = 0; i < forms.size(); i++) {
            if ("10-Q".equals(forms.get(i))) {
                return accessions.get(i).replace("-", "") + "|" + dates.get(i);
            }
        }
        throw new RuntimeException("No 10-Q found for ticker: " + ticker);
    }

    public String fetchTranscriptText(String ticker) {
        String result = getLatestFilingAccession(ticker);
        String[] parts = result.split("\\|");
        String accession = parts[0];
        String cik = getCikFromTicker(ticker);

        String indexUrl = "https://www.sec.gov/Archives/edgar/full-index/" +
                "https://data.sec.gov/submissions/CIK" + cik + ".json";

        String docUrl = "https://www.sec.gov/Archives/edgar/data/" +
                cik.replaceFirst("^0+", "") + "/" + accession + "/" +
                accession + ".txt";

        try {
            return restTemplate.getForObject(docUrl, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch filing text: " + e.getMessage());
        }
    }
}