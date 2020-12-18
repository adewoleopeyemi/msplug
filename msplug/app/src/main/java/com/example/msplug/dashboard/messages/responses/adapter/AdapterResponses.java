package com.example.msplug.dashboard.messages.responses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msplug.R;
import com.example.msplug.dashboard.messages.responses.models.ModelResponse;

import java.util.ArrayList;

public class AdapterResponses extends RecyclerView.Adapter<AdapterResponses.Holder> {
    private Context context;
    private ArrayList<ModelResponse> responses;


    public AdapterResponses(Context context, ArrayList<ModelResponse> responses) {
        this.context = context;
        this.responses = responses;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_response, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.date.setText(responses.get(position).getRequest_date());
        holder.responsemessage.setText(responses.get(position).getResponse_message());
        holder.responseCount.setText(""+position);
        holder.ussd.setText(responses.get(position).getCommand());
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView ussd, responsemessage, responseCount, date;
        public Holder(@NonNull View itemView) {
            super(itemView);
            ussd = itemView.findViewById(R.id.USSD);
            responseCount = itemView.findViewById(R.id.responseCount);
            responsemessage = itemView.findViewById(R.id.responsemessage);
            date = itemView.findViewById(R.id.date);
        }
    }
}
