package com.liedetector.liedetector;

import jakarta.persistence.*;

@Entity
@Table(name = "transparency_scores")
public class TransparencyScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "earnings_call_id", nullable = false)
    private EarningsCall earningsCall;

    private int hedgeCount;
    private int passiveCount;
    private double sentimentCompound;
    private double transparencyScore;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EarningsCall getEarningsCall() { return earningsCall; }
    public void setEarningsCall(EarningsCall earningsCall) { this.earningsCall = earningsCall; }

    public int getHedgeCount() { return hedgeCount; }
    public void setHedgeCount(int hedgeCount) { this.hedgeCount = hedgeCount; }

    public int getPassiveCount() { return passiveCount; }
    public void setPassiveCount(int passiveCount) { this.passiveCount = passiveCount; }

    public double getSentimentCompound() { return sentimentCompound; }
    public void setSentimentCompound(double sentimentCompound) { this.sentimentCompound = sentimentCompound; }

    public double getTransparencyScore() { return transparencyScore; }
    public void setTransparencyScore(double transparencyScore) { this.transparencyScore = transparencyScore; }
}