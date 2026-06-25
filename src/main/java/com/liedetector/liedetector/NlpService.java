package com.liedetector.liedetector;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NlpService {

    private final RestTemplate restTemplate;
    private static final String NLP_URL = "http://localhost:8001/analyze";

    public NlpService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> analyze(String transcriptText) {
        Map<String, String> request = Map.of("text", transcriptText);
        return restTemplate.postForObject(NLP_URL, request, Map.class);
    }
}