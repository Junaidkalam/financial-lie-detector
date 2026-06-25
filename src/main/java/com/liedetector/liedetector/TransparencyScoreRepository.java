package com.liedetector.liedetector;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransparencyScoreRepository extends JpaRepository<TransparencyScore, Long> {
    Optional<TransparencyScore> findByEarningsCallId(Long earningsCallId);
}