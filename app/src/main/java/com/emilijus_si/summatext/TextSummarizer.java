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
        return Arrays.asList(text.split("\\.\\s*"));
    }

    private Map<String, Integer> computeWordFrequencies(String text) throws IOException {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text))) {
            CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttr.toString().toLowerCase();
                wordFrequencies.put(term, wordFrequencies.getOrDefault(term, 0) + 1);
            }
            tokenStream.end();
        }
        return wordFrequencies;
    }

    private Map<String, Integer> scoreSentences(List<String> sentences, Map<String, Integer> wordFrequencies) throws IOException {
        Map<String, Integer> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            int score = 0;
            try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(sentence))) {
                CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    String term = charTermAttr.toString().toLowerCase();
                    score += wordFrequencies.getOrDefault(term, 0);
                }
                tokenStream.end();
            }
            sentenceScores.put(sentence, score + calculatePositionScore(sentences, sentence));
        }
        return sentenceScores;
    }

    private int calculatePositionScore(List<String> sentences, String sentence) {
        int index = sentences.indexOf(sentence);
        int totalSentences = sentences.size();
        if (index == 0 || index == totalSentences - 1) {
            return 5;
        }
        if (index <= totalSentences * 0.2 || index >= totalSentences * 0.8) {
            return 3;
        }
        return 1;
    }

    private String getTopSentences(List<String> sentences, Map<String, Integer> sentenceScores, int summarySize) {
        return sentences.stream()
                .sorted((s1, s2) -> sentenceScores.get(s2).compareTo(sentenceScores.get(s1)))
                .distinct()
                .limit(summarySize)
                .collect(Collectors.joining(". ")) + ".";
    }
}