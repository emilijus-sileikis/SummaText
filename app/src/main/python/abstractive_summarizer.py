from transformers import pipeline

model_name = "cnicu/t5-small-booksum"
summarizer = pipeline("summarization", model=model_name)

def summarize_text(text):
    min_word_threshold = 50

    if len(text.split()) < min_word_threshold:
        return "Input text is too short for summarization. Preferred minimum word length is at least 50 words."

    summary = summarizer(text, num_beams=4, no_repeat_ngram_size=3, max_length=512, min_length=30, length_penalty=2.0, temperature=0.8, truncation=True)
    return summary[0]['summary_text']