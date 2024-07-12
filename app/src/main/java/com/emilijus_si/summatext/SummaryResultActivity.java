package com.emilijus_si.summatext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SummaryResultActivity extends AppCompatActivity {

    private TextView summaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        summaryTextView = findViewById(R.id.summaryTextView);

        // Get the summary text from the intent
        String summary = getIntent().getStringExtra("SUMMARY_TEXT");
        summaryTextView.setText(summary);

        Button copyButton = findViewById(R.id.copyButton);
        Button saveButton = findViewById(R.id.saveButton);

        copyButton.setOnClickListener(v -> copySummaryToClipboard());
        saveButton.setOnClickListener(v -> {
            // Implement save functionality
            Toast.makeText(this, "Save summary feature not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu); // Inflate your menu here
        return true;
    }

    private void copySummaryToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Summary", summaryTextView.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Summary copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}