package com.liedetector.liedetector;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/earnings-calls")
public class EarningsCallController {

    private final EarningsCallRepository earningsCallRepository;
    private final CompanyRepository companyRepository;

    public EarningsCallController(EarningsCallRepository earningsCallRepository,
                                  CompanyRepository companyRepository) {
        this.earningsCallRepository = earningsCallRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<EarningsCall> getAll() {
        return earningsCallRepository.findAll();
    }

    @GetMapping("/company/{companyId}")
    public List<EarningsCall> getByCompany(@PathVariable Long companyId) {
        return earningsCallRepository.findByCompanyId(companyId);
    }

    @PostMapping
    public EarningsCall create(@RequestBody EarningsCallRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        EarningsCall call = new EarningsCall();
        call.setCompany(company);
        call.setQuarter(request.getQuarter());
        call.setYear(request.getYear());
        call.setCallDate(request.getCallDate());
        call.setTranscriptText(request.getTranscriptText());

        return earningsCallRepository.save(call);
    }
}