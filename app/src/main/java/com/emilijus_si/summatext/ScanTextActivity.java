package com.emilijus_si.summatext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class ScanTextActivity extends AppCompatActivity {

    private static final String TAG = "ScanTextActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_text);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Assume that you have the scanned text
        String scannedText = "Your scanned text goes here";

        try {
            String summary = summarizeText(scannedText);
            Log.d(TAG, "Summary: " + summary);

            // Navigate to SummaryResultActivity with the summary text
            Intent intent = new Intent(ScanTextActivity.this, SummaryResultActivity.class);
            intent.putExtra("SUMMARY_TEXT", summary);
            startActivity(intent);

        } catch (IOException e) {
            Log.e(TAG, "Error summarizing text", e);
        }
    }

    private String summarizeText(String userInput) throws IOException {
        if (userInput == null || userInput.isEmpty()) {
            return "Input text is empty.";
        }
        TextSummarizer summarizer = new TextSummarizer();
        return summarizer.summarizeText(userInput, 3); // Summarize to 3 sentences, change later
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        return super.onOptionsItemSelected(item);
    }
}
