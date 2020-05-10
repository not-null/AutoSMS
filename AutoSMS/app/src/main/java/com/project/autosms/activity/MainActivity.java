package com.project.autosms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.autosms.R;
import com.project.autosms.model.Position;
import com.project.autosms.adapter.RecViewAdapter;
import com.project.autosms.model.Record;
import com.project.autosms.model.ResponseMapping;
import com.project.autosms.util.SerializeHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecViewAdapter.OnRecordListener {
    private final int RECEIVE_SMS_CODE = 420;
    private final int SEND_SMS_CODE = 1337;

    private ArrayList<Record> records;

    private RecViewAdapter rwAdapter;

    //For backup
    private int lastDelete;
    private Record deletedRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewNumberActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        RecyclerView rw = (RecyclerView) findViewById(R.id.list);
        rw.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rw);

        addTestData();

        Switch mySwitch = (Switch)findViewById(R.id.simpleSwitch);
        mySwitch.setChecked(true);

        //PERMISSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_CODE);
                if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String nr = data.getStringExtra("nr");
            records.add(new Record(nr));
            SerializeHandler.saveObject(getApplicationContext(), records, "responses");
            rwAdapter.notifyItemInserted(records.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        records = SerializeHandler.readObject(this, "responses");
        rwAdapter = new RecViewAdapter(this, records, this);
        RecyclerView rw = (RecyclerView) findViewById(R.id.list);
        rw.setAdapter(rwAdapter);

        Log.i("AAAA", records.get(0).getNr());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SEND_SMS_CODE || requestCode == RECEIVE_SMS_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            addTestData();
            rwAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTestData() {
        Record r1 = new Record("+46793358714", new ResponseMapping("Hej", "Hej på dig!", Position.CONTAINS));
        r1.addResponseMapping(new ResponseMapping("Tja", "Tja tja", Position.CONTAINS));
        r1.addResponseMapping(new ResponseMapping("Hejdå", "Vi ses!", Position.CONTAINS));

        Record r2 = new Record("+46738295802", new ResponseMapping("Hej", "Hej på dig!", Position.STARTS));
        r2.addResponseMapping(new ResponseMapping("Hur mår du?", "Jag mår bra", Position.CONTAINS));
        r2.addResponseMapping(new ResponseMapping("vi ses", "Hejdå, vi ses!", Position.ENDS));

        Record r3 = new Record("+46702846192", new ResponseMapping("Hej", "Hej på dig!", Position.STARTS));
        r3.addResponseMapping(new ResponseMapping("Puss", "Puss puss", Position.CONTAINS));
        r3.addResponseMapping(new ResponseMapping("Kram", "Kram mitt hjärta", Position.ENDS));

        Record r4 = new Record("+46751720457", new ResponseMapping("Hej", "Hej, pappa!", Position.CONTAINS));
        r4.addResponseMapping(new ResponseMapping("Hur mår du", "Bara bra, tack!", Position.CONTAINS));
        r4.addResponseMapping(new ResponseMapping("Är du hungrig", "Självklart!", Position.CONTAINS));

        records = new ArrayList<>();
        records.add(r1);
        records.add(r2);
        records.add(r3);
        records.add(r4);

        SerializeHandler.saveObject(this, records, "responses");
        ArrayList<Record> test = SerializeHandler.readObject(this, "responses");
        System.out.println(test.get(0).getResponseMappings().get(0).getString());
    }

    @Override
    public void onRecordClick(int position) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("records", records);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            lastDelete = viewHolder.getAdapterPosition();
            deletedRecord = records.remove(lastDelete);
            rwAdapter.notifyItemRemoved(lastDelete);

            SerializeHandler.saveObject(getApplicationContext(), records, "responses");

            Snackbar.make(viewHolder.itemView, "1 item removed", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new MainActivity.UndoListener())
                    .show();
        }
    };

    private class UndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            records.add(lastDelete, deletedRecord);
            SerializeHandler.saveObject(getApplicationContext(), records, "responses");
            rwAdapter.notifyItemInserted(lastDelete);
        }
    }
}