package com.project.autosms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.autosms.R;
import com.project.autosms.model.Record;

import java.util.ArrayList;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.ViewHolder> {

    private ArrayList<Record> records;
    private Context context;

    private OnRecordListener onRecordListener;

    // data is passed into the constructor
    public RecViewAdapter(Context context, ArrayList<Record> data, OnRecordListener onRecordListener) {
        this.context = context;
        this.records = data;

        this.onRecordListener = onRecordListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, onRecordListener);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return records.size();
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.phoneNr.setText(record.getNr());
        holder.nrOfRecords.setText(record.getResponseMappings().size() + " response" + (record.getResponseMappings().size() != 1 ? "s" : ""));

        Log.i("---KOLLA---", "Binded " + record.getNr() + " with " + record.getResponseMappings().size() + " records.");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView phoneNr;
        public TextView nrOfRecords;
        public LinearLayout recordItem;

        private OnRecordListener onRecordListener;

        public ViewHolder(@NonNull View itemView, OnRecordListener onRecordListener) {
            super(itemView);

            this.phoneNr = (TextView) itemView.findViewById(R.id.phoneNr);
            this.nrOfRecords = (TextView) itemView.findViewById(R.id.nrOfRecords);

            this.recordItem = (LinearLayout) itemView.findViewById(R.id.recordItem);

            this.onRecordListener = onRecordListener;

            recordItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("BOOMBOOM", "onClick: CLICK CLACK");
            onRecordListener.onRecordClick(getAdapterPosition());
        }
    }

    public interface OnRecordListener {
        void onRecordClick(int position);
    }

}