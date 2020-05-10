package com.project.autosms.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.autosms.R;
import com.project.autosms.adapter.NrRecViewAdapter;
import com.project.autosms.model.Record;
import com.project.autosms.model.ResponseMapping;
import com.project.autosms.util.SerializeHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {
    private ArrayList<Record> records;
    private int position;
    private NrRecViewAdapter rwAdapter;

    // For backup, when restoring deleted items
    private int lastDelete;
    private ResponseMapping deletedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get data from previous activity
        this.records = (ArrayList<Record>) getIntent().getSerializableExtra("records");
        this.position = getIntent().getIntExtra("position", 0);

        // Setup the recycler view
        RecyclerView rw = (RecyclerView) findViewById(R.id.list);
        rwAdapter = new NrRecViewAdapter(this, records, position);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, llm.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        rw.setAdapter(rwAdapter);
        rw.setLayoutManager(llm);
        rw.addItemDecoration(dividerItemDecoration);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rw);

        // Set the title
        TextView nrTitle = findViewById(R.id.phoneNrTitle);
        nrTitle.setText(records.get(position).getNr());

        // Setup "add" button
        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordActivity.this, NewResponseActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if a new response has been added, and save it
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            ResponseMapping rm = (ResponseMapping) data.getSerializableExtra("response");
            records.get(position).addResponseMapping(rm);
            SerializeHandler.saveObject(getApplicationContext(), records, "responses");
            rwAdapter.notifyItemInserted(records.get(position).getResponseMappings().size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO: Implement settings
            // ...
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle swiping to delete list items
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            lastDelete = viewHolder.getAdapterPosition();
            deletedResponse = records.get(position).getResponseMappings().remove(lastDelete);
            rwAdapter.notifyItemRemoved(lastDelete);

            SerializeHandler.saveObject(getApplicationContext(), records, "responses");

            Snackbar.make(viewHolder.itemView, "1 item removed", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new UndoListener())
                    .show();
        }
    };

    private class UndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            records.get(position).getResponseMappings().add(lastDelete, deletedResponse);
            SerializeHandler.saveObject(getApplicationContext(), records, "responses");
            rwAdapter.notifyItemInserted(lastDelete);
        }
    }
}
