package com.emilijus_si.summatext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class TextSummarizer {

    private Analyzer analyzer;

    public TextSummarizer() {
        this.analyzer = new WhitespaceAnalyzer();
    }

    public String summarizeText(String text, int summarySize) throws IOException {
        List<String> sentences = splitIntoSentences(text);
        Map<String, Integer> wordFrequencies = computeWordFrequencies(text);
        Map<String, Integer> sentenceScores = scoreSentences(sentences, wordFrequencies);

        return getTopSentences(sentences, sentenceScores, summarySize);
    }

    private List<String> splitIntoSentences(String text) {
        // Simple sentence splitter based on period followed by space
        return Arrays.asList(text.split("\\.\\s*"));
    }

    private Map<String, Integer> computeWordFrequencies(String text) throws IOException {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttr.toString().toLowerCase();
            wordFrequencies.put(term, wordFrequencies.getOrDefault(term, 0) + 1);
        }
        tokenStream.end();
        tokenStream.close();
        return wordFrequencies;
    }

    private Map<String, Integer> scoreSentences(List<String> sentences, Map<String, Integer> wordFrequencies) throws IOException {
        Map<String, Integer> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            int score = 0;
            TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(sentence));
            CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttr.toString().toLowerCase();
                score += wordFrequencies.getOrDefault(term, 0);
            }
            tokenStream.end();
            tokenStream.close();
            sentenceScores.put(sentence, score);
        }
        return sentenceScores;
    }

    private String getTopSentences(List<String> sentences, Map<String, Integer> sentenceScores, int summarySize) {
        return sentences.stream()
                .sorted((s1, s2) -> sentenceScores.get(s2).compareTo(sentenceScores.get(s1)))
                .limit(summarySize)
                .collect(Collectors.joining(". "));
    }

    public static void main(String[] args) {
        try {
            TextSummarizer summarizer = new TextSummarizer();
            String text = "The development of artificial intelligence (AI) has significantly impacted various sectors, including healthcare, finance, and transportation. AI technologies, such as machine learning and natural language processing, are being used to automate tasks, analyze large datasets, and make predictions. In healthcare, AI is improving diagnostics accuracy, personalized treatment plans, and drug discovery processes. Financial institutions utilize AI for fraud detection, algorithmic trading, and customer service automation. Autonomous vehicles are a result of AI advancements in the transportation sector, promising safer and more efficient travel. Overall, AI continues to transform industries worldwide, driving innovation and efficiency.";

            String summary = summarizer.summarizeText(text, 3); // Summarize to 3 sentences
            System.out.println("Summary: ");
            System.out.println(summary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
