package com.project.autosms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.project.autosms.model.Position;
import com.project.autosms.R;
import com.project.autosms.model.ResponseMapping;

public class NewResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_response);

        //Default result if it isn't saved
        setResult(RESULT_CANCELED);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.positions, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);

        Button saveButton =(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Check if allowed also..

                String message = ((EditText) findViewById(R.id.message)).getText().toString();
                String response = ((EditText) findViewById(R.id.response)).getText().toString();
                Position pos = Position.valueOf(((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
                ResponseMapping rm = new ResponseMapping(message, response, pos);

                Intent intent = getIntent();
                intent.putExtra("response", rm);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
