package com.project.autosms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.autosms.R;
import com.project.autosms.model.Record;
import com.project.autosms.model.ResponseMapping;

import java.util.ArrayList;

public class NrRecViewAdapter extends RecyclerView.Adapter<NrRecViewAdapter.ViewHolder> {

    private Record record;
    private Context context;

    // For writing to file
    private ArrayList<Record> records;

    public NrRecViewAdapter(Context context, ArrayList<Record> records, int position) {
        this.context = context;
        this.records = records;
        this.record = records.get(position);
    }

    // Inflates the row layout from XML when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nr_list_row, parent, false);

        return new ViewHolder(view);
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return record.getResponseMappings().size();
    }

    // Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResponseMapping rm = record.getResponseMappings().get(position);
        holder.incomingMessage.setText(String.format("%s \"%s\"", rm.getPosition().toString(), rm.getString()));
        holder.response.setText(String.format("Response: \"%s\"", rm.getResponse()));
    }

    // The object that holds the individual record
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView incomingMessage;
        public TextView response;
        public LinearLayout responseItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.incomingMessage = (TextView) itemView.findViewById(R.id.incomingMessage);
            this.response = (TextView) itemView.findViewById(R.id.response);

            this.responseItem = (LinearLayout) itemView.findViewById(R.id.responseItem);
        }
    }
}