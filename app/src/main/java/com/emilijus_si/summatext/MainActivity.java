package com.emilijus_si.summatext;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

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
        return summarizer.summarizeText(userInput, 3); // Summarize to 3 sentences change later
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}