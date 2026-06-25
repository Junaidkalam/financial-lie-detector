package com.liedetector.liedetector;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyRepository repository;

    public CompanyController(CompanyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Company> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Company create(@RequestBody Company company) {
        return repository.save(company);
    }
}