package com.project.autosms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.project.autosms.R;

public class NewNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_number);

        // Default result if it isn't saved
        setResult(RESULT_CANCELED);

        // When the user clicks "save"
        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //TODO: Check if allowed (no/faulty input etc.)

                String nr = ((EditText) findViewById(R.id.nr)).getText().toString();

                Intent intent = getIntent();
                intent.putExtra("nr", nr);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}