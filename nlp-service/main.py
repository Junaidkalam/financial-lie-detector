from fastapi import FastAPI
from pydantic import BaseModel
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
import spacy

app = FastAPI()
nlp = spacy.load("en_core_web_sm")
analyzer = SentimentIntensityAnalyzer()

HEDGE_WORDS = [
    "maybe", "perhaps", "possibly", "might", "could", "we believe",
    "we think", "we expect", "approximately", "around", "roughly",
    "uncertain", "unclear", "challenging", "difficult"
]

class TranscriptRequest(BaseModel):
    text: str

@app.post("/analyze")
def analyze(request: TranscriptRequest):
    text = request.text.lower()
    doc = nlp(request.text)

    # Hedge word count
    hedge_count = sum(text.count(word) for word in HEDGE_WORDS)

    # Passive voice count
    passive_count = sum(
        1 for token in doc
        if token.dep_ == "auxpass"
    )

    # Sentiment
    sentiment = analyzer.polarity_scores(request.text)
    compound_score = sentiment["compound"]

    # Transparency Score (0-100)
    transparency_score = max(0, min(100,
                                    100
                                    - (hedge_count * 5)
                                    - (passive_count * 3)
                                    + (compound_score * 10)
                                    ))

    return {
        "hedge_count": hedge_count,
        "passive_count": passive_count,
        "sentiment_compound": compound_score,
        "transparency_score": round(transparency_score, 2)
    }