package com.liedetector.liedetector;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EarningsCallRepository extends JpaRepository<EarningsCall, Long> {
    List<EarningsCall> findByCompanyId(Long companyId);
}