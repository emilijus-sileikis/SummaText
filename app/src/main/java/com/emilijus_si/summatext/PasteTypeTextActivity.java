package com.emilijus_si.summatext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class PasteTypeTextActivity extends AppCompatActivity {

    private static final String TAG = "PasteTypeTextActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_type_text);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText inputField = findViewById(R.id.inputField);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputField.getText().toString().trim();
                try {
                    String summary = summarizeText(userInput);

                    Intent intent = new Intent(PasteTypeTextActivity.this, SummaryResultActivity.class);
                    intent.putExtra("SUMMARY_TEXT", summary);
                    startActivity(intent);

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
        return summarizer.summarizeText(userInput, 3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}