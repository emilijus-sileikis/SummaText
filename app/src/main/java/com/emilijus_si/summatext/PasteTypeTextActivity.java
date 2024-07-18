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
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class PasteTypeTextActivity extends AppCompatActivity {

    private static final String TAG = "PasteTypeTextActivity";
    private EditText numberOfSentencesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_type_text);

        EditText inputField = findViewById(R.id.inputField);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputField.getText().toString().trim();

                if (userInput.isEmpty()) {
                    Toast.makeText(PasteTypeTextActivity.this, "Please enter text to summarize", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show overlay with progress indicator and text
                FrameLayout progressOverlay = findViewById(R.id.progressOverlay);
                progressOverlay.setVisibility(View.VISIBLE);

                // Execute summarization in background thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Python py = Python.getInstance();
                            PyObject pyObject = py.getModule("abstractive_summarizer");

                            // Perform the summarization
                            String summary = pyObject.callAttr("summarize_text", userInput).toString();

                            // Hide overlay and launch SummaryResultActivity
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressOverlay.setVisibility(View.GONE);
                                    Intent intent = new Intent(PasteTypeTextActivity.this, SummaryResultActivity.class);
                                    intent.putExtra("SUMMARY_TEXT", summary);
                                    startActivity(intent);
                                }
                            });
                        } catch (PyException e) {
                            Log.e(TAG, "Error summarizing text", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hide overlay on error
                                    progressOverlay.setVisibility(View.GONE);
                                    Toast.makeText(PasteTypeTextActivity.this, "Error summarizing text", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (NumberFormatException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hide overlay on error
                                    progressOverlay.setVisibility(View.GONE);
                                    Toast.makeText(PasteTypeTextActivity.this, "Invalid number of sentences", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
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