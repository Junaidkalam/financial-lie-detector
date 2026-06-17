# Financial Lie Detector

An NLP-powered earnings call transparency analyzer that detects linguistic patterns associated with executive transparency, evasiveness, and potential deception.

## Problem

Public company executives often communicate differently when discussing negative events. Rather than directly lying, they may use hedge words, passive voice, vague statements, or deflection.

Analyzing thousands of earnings call transcripts manually is impossible for most investors.

## Solution

Financial Lie Detector automatically analyzes earnings call transcripts and generates a Transparency Score based on:

* Hedge word usage
* Passive voice detection
* Sentiment analysis
* Numerical specificity
* Ownership language
* Directness of communication

## Architecture

Transcript → Spring Boot Backend → Python NLP Engine → Transparency Score → Dashboard

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Data JPA

### Database

* PostgreSQL

### NLP

* Python
* spaCy
* VADER

### Frontend

* React
* Tailwind CSS

## Status

🚧 Under Development
