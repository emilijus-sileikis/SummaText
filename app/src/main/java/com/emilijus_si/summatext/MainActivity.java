package com.emilijus_si.summatext;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText inputField = findViewById(R.id.inputField);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputField.getText().toString().trim();
                try {
                    String summary = summarizeText(userInput);
                    Log.d(TAG, "Summary: " + summary);
                } catch (IOException e) {
                    Log.e(TAG, "Error summarizing text", e);
                }
            }
        });
    }

    private String summarizeText(String userInput) throws IOException {
        if (userInput == null || userInput.isEmpty()) {
            return "Input text is empty.";
        }
        TextSummarizer summarizer = new TextSummarizer();
        return summarizer.summarizeText(userInput, 3); // Summarize to 3 sentences
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // TextSummarizer class implementing Lucene summarization logic
    public class TextSummarizer {
        private final Analyzer analyzer;

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
    }
}