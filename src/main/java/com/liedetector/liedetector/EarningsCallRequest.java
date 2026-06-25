package com.liedetector.liedetector;

import java.time.LocalDate;

public class EarningsCallRequest {
    private Long companyId;
    private String quarter;
    private int year;
    private LocalDate callDate;
    private String transcriptText;

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public LocalDate getCallDate() { return callDate; }
    public void setCallDate(LocalDate callDate) { this.callDate = callDate; }

    public String getTranscriptText() { return transcriptText; }
    public void setTranscriptText(String transcriptText) { this.transcriptText = transcriptText; }
}