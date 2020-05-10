package com.project.autosms.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.autosms.R;
import com.project.autosms.adapter.RecViewAdapter;
import com.project.autosms.model.Record;
import com.project.autosms.util.SerializeHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecViewAdapter.OnRecordListener {
    private final int RECEIVE_SMS_CODE = 1336;
    private final int SEND_SMS_CODE = 1337;

    private ArrayList<Record> records;

    private RecViewAdapter rwAdapter;

    // For backup when restoring deleted items
    private int lastDelete;
    private Record deletedRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup "add" button
        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewNumberActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Setup numbers list
        RecyclerView rw = (RecyclerView) findViewById(R.id.list);
        rw.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rw);

        // Setup switch
        Switch mySwitch = (Switch)findViewById(R.id.simpleSwitch);
        mySwitch.setChecked(true);
        //TODO: Implement on/off functionality

        // Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if a new number has been added, and save it
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

        // Update listings
        records = SerializeHandler.readObject(this, "responses");
        if (records == null) records = new ArrayList<>();
        rwAdapter = new RecViewAdapter(this, records, this);
        RecyclerView rw = (RecyclerView) findViewById(R.id.list);
        rw.setAdapter(rwAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SEND_SMS_CODE || requestCode == RECEIVE_SMS_CODE){
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO: Implement settings
            // ...
            // Send the response
            android.os.SystemClock.sleep(5000);
            sendNotif();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendNotif(){
        // Notify the user
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Responded to 072342323")
                .setContentText("\"" + "bye" + "\"")
                .setSmallIcon(R.mipmap.transparent_icon)
                .setColorized(true)
                .setColor(Color.parseColor("#292f46"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            NotificationChannel notificationChannel = new NotificationChannel("1" , "Response", NotificationManager.IMPORTANCE_DEFAULT) ;
            mBuilder.setChannelId("1");
            assert nm != null;
            nm.createNotificationChannel(notificationChannel) ;
        }

        // If the user clicks on the notification
        Intent notificationIntent = new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent conPendingIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(conPendingIntent);

        nm.notify(16712, mBuilder.build());
    }

    @Override
    public void onRecordClick(int position) {
        // When you click on a list item
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("records", records);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    // Handle swiping to delete list items
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