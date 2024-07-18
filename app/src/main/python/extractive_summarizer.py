from gensim.summarization.summarizer import summarize

def summarize_text(text):
    gen_summary = summarize(text)
    return gen_summary