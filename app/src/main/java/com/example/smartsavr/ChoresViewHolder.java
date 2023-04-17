package com.example.smartsavr;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ChoresViewHolder extends RecyclerView.ViewHolder {

    public TextView taskName;
    public TextView dateText;
    public TextView rewardCents;

    public ImageView imageCircle;
    public ImageView done;

    public TextView doneText;

    public ImageView delete;
    public ImageView edit;
    public ImageView undo;
    public ImageView taskCompleted;

    public CardView taskCardview;

    public TextView dollar;

    public ChoresViewHolder(@NonNull View itemView) {
        super(itemView);
        this.taskName = itemView.findViewById(R.id.activityName);
        this.rewardCents = itemView.findViewById(R.id.reward);
        this.dateText = itemView.findViewById(R.id.dateText);
        this.done = itemView.findViewById(R.id.done);
        this.doneText = itemView.findViewById(R.id.textDone);
        this.delete = itemView.findViewById(R.id.delete);
        this.edit = itemView.findViewById(R.id.edit);
        this.undo = itemView.findViewById(R.id.undo);
        this.taskCompleted = itemView.findViewById(R.id.taskDone);
        this.taskCardview = itemView.findViewById(R.id.activityCard);

    }
}
