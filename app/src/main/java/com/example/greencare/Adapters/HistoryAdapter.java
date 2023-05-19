package com.example.greencare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greencare.Classes.HistoryListItems;
import com.example.greencare.R;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<HistoryListItems> listItems;

    public HistoryAdapter(List<HistoryListItems> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryListItems listItem = listItems.get(position);
        holder.imageView.setImageBitmap(listItem.getBitmap());
        holder.resultTv.setText(listItem.getResult());
        holder.stageTv.setText(listItem.getStage());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView resultTv, stageTv;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageId);
            resultTv = (TextView) v.findViewById(R.id.resultId);
            stageTv = (TextView) v.findViewById(R.id.stageId);
        }
    }
}