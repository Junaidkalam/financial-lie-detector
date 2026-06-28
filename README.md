# Financial Lie Detector

An NLP-powered financial disclosure analyzer that evaluates the transparency of corporate communications using linguistic analysis of SEC filings.

## Problem

Public companies communicate large amounts of financial information through regulatory filings. Identifying patterns such as hedge language, passive voice, vague statements, and sentiment across lengthy documents is time-consuming and difficult to perform manually.

## Solution

Financial Lie Detector automatically retrieves SEC filings from the EDGAR database and analyzes them using NLP techniques to generate a Transparency Score based on:

- Hedge word usage
- Passive voice detection
- Sentiment analysis
- Numerical specificity
- Ownership language
- Communication directness

## Architecture

SEC EDGAR API
→ Spring Boot Backend
→ Python NLP Engine
→ PostgreSQL
→ React Dashboard

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA

### Database
- PostgreSQL

### NLP
- Python
- FastAPI
- spaCy
- NLTK (VADER)

### Frontend
- React
- Tailwind CSS

## Project Status

🚧 Under Development

The backend, NLP pipeline, and SEC EDGAR integration are fully functional. The current React frontend serves as a temporary prototype while I learn React and will be progressively replaced with my own implementation.

**Future Enhancement:** Support for earnings call transcripts and additional financial data sources (e.g., SEBI/BSE).
