package com.example.smartsavr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChoresAdapter extends RecyclerView.Adapter<ChoresViewHolder> {

    private static final String CHILDREN = "children";

    Context context;

    List<Chore> chores;

    boolean isChildUser;

    DBReference dbReference;
    private final String childId;


    public ChoresAdapter(List<Chore> chores, Context context, boolean isChildUser, DBReference dbReference, String childId) {
        this.context = context;
        this.chores = chores;
        this.isChildUser = isChildUser;
        this.dbReference = dbReference;
        this.childId = childId;
    }

    @NonNull
    @Override
    public ChoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChoresViewHolder(LayoutInflater.from(context).inflate(R.layout.chore_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChoresViewHolder holder, int position) {
        holder.taskCardview.setCardBackgroundColor(Color.WHITE);
        Chore chore = chores.get(holder.getAdapterPosition());
        holder.taskName.setText(chore.getTaskName());

        int rewardCents = chore.getRewardCents();
        if (rewardCents == 0) {
            holder.rewardCents.setVisibility(View.GONE);
        } else {
            holder.rewardCents.setVisibility(View.VISIBLE);
            holder.rewardCents.setText(Utils.centsToDollarString(rewardCents));
        }

        if (isChildUser) {


            if (!chore.isComplete()) {

                if (chore.getDeadline() > System.currentTimeMillis()) {
                    holder.dateText.setText(String.format("Due in %s", getDateToStringProcessor(chore.getDeadline())));
                } else {
                    holder.dateText.setText(String.format("Overdue by %s", getDateToStringProcessor(chore.getDeadline())));
                }
                holder.done.setImageResource(R.drawable.tick);
                holder.done.setVisibility(View.VISIBLE);
                holder.doneText.setVisibility(View.VISIBLE);
                holder.doneText.setText(R.string.mark_done);

            } else {
                if (!chore.isApproved()) {
                    if (chore.getDeadline() > chore.getCompletedTimestamp()) {
                        holder.dateText.setText(String.format("Completed %s ago", getDateToStringProcessor(chore.getCompletedTimestamp())));
                    } else {
                        holder.dateText.setText(String.format("Completed %s ago, overdue by %s", getDateToStringProcessor(chore.getCompletedTimestamp()), getDateToStringProcessor(chore.getCompletedTimestamp(), chore.getDeadline())));
                    }
                    holder.done.setImageResource(R.drawable.greentick);
                    holder.done.setVisibility(View.VISIBLE);
                    holder.doneText.setVisibility(View.VISIBLE);
                    holder.doneText.setText(R.string.tap_to_undo);
                } else {
                    holder.dateText.setText(String.format("Approved %s ago", getDateToStringProcessor(chore.getApprovedTimestamp())));
                    holder.done.setVisibility(View.INVISIBLE);
                    holder.doneText.setVisibility(View.INVISIBLE);
                    holder.taskCardview.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            holder.done.setOnClickListener(v -> {

                if (!chore.isComplete()) {

                    chore.setComplete(true);
                    chore.setCompletedTimestamp(System.currentTimeMillis());
                    dbReference.getCollectionReference().document(chore.getId()).set(chore);
                } else {
                    chore.setComplete(false);
                    chore.setCompletedTimestamp(Integer.MAX_VALUE);
                    dbReference.getCollectionReference().document(chore.getId()).set(chore);
                }
            });

            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.taskCompleted.setVisibility(View.GONE);
            holder.undo.setVisibility(View.GONE);

        }
        // Parent starts

        else {
            holder.done.setVisibility(View.INVISIBLE);
            holder.doneText.setVisibility(View.INVISIBLE);
            holder.delete.setImageResource(R.drawable.trash);
            holder.edit.setImageResource(R.drawable.pencil);
            holder.undo.setImageResource(R.drawable.undo);
            holder.taskCompleted.setImageResource(R.drawable.check);
            if (!chore.isComplete()) {
                if (chore.getDeadline() > System.currentTimeMillis()) {
                    holder.dateText.setText(String.format("Due in %s", getDateToStringProcessor(chore.getDeadline())));
                } else {
                    holder.dateText.setText(String.format("Overdue by %s", getDateToStringProcessor(chore.getDeadline())));
                }
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
                holder.taskCompleted.setVisibility(View.INVISIBLE);
                holder.undo.setVisibility(View.INVISIBLE);

                Log.d("VISIBILITY", Integer.toString(holder.delete.getVisibility()));


                holder.delete.setOnClickListener(v -> dbReference.getCollectionReference().document(chore.getId()).delete());

                holder.edit.setOnClickListener(v -> {


                    ChoreBottomSheetDialog bottomSheet = new ChoreBottomSheetDialog(chore);
                    bottomSheet.show(ParentChildChoresActivity.getSupportFragmentManagerParent(), ChoreBottomSheetDialog.TAG);


                });

            } else {
                holder.taskCompleted.setVisibility(View.VISIBLE);
                if (!chore.isApproved()) {
                    if (chore.getDeadline() > chore.getCompletedTimestamp()) {
                        holder.dateText.setText(String.format("Completed %s ago", getDateToStringProcessor(chore.getCompletedTimestamp())));
                    } else {
                        holder.dateText.setText(String.format("Completed %s ago, overdue by %s", getDateToStringProcessor(chore.getCompletedTimestamp()), getDateToStringProcessor(chore.getCompletedTimestamp(), chore.getDeadline())));
                    }
                    holder.delete.setVisibility(View.INVISIBLE);
                    holder.edit.setVisibility(View.INVISIBLE);
                    holder.taskCompleted.setVisibility((View.VISIBLE));
                    holder.undo.setVisibility(View.VISIBLE);
                    holder.taskCompleted.setOnClickListener(v -> {
                        Snackbar.make(context,v, "Do you want to approve the chore?", Snackbar.LENGTH_LONG)
                                .setAction("APPROVE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        chore.setApproved(true);
                                        chore.setApprovedTimestamp(System.currentTimeMillis());

                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                        firebaseFirestore.collection(CHILDREN).document(childId).get().addOnCompleteListener(
                                                task -> {
                                                    Log.d("ChoresAdapter", String.format("Got snapshot of child with ID %s", childId));
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot value = task.getResult();
                                                        if (value != null) {
                                                            Child child = value.toObject(Child.class);
                                                            if (child != null) {
                                                                int newBalanceCents = child.getAccountBalanceCents() + chore.getRewardCents();
                                                                Log.d("ChoresAdapter", String.format("Set %s's balance to %d cents + %d cents = %d cents", child.getName(), child.getAccountBalanceCents(), chore.getRewardCents(), newBalanceCents));
                                                                child.setAccountBalanceCents(newBalanceCents);
                                                                ParentChildDetailActivity.child.setAccountBalanceCents(newBalanceCents);
                                                                firebaseFirestore.collection(CHILDREN).document(childId).set(child).addOnSuccessListener(
                                                                                documentReference -> {
                                                                                    Log.d("ChoresAdapter", String.format("Successfully saved %s's profile", child.getName()));
                                                                                    firebaseFirestore.collection(CHILDREN).document(childId).get().addOnCompleteListener(t -> {
                                                                                        if (t.isSuccessful()) {
                                                                                            Log.d("ChoresAdapter", String.format("new account balance cents: %d", t.getResult().toObject(Child.class).getAccountBalanceCents()));
                                                                                        }
                                                                                    });
                                                                                })
                                                                        .addOnFailureListener(e -> Log.e("ChoresAdapter", "Error writing document", e));
                                                                Log.d("ChoresAdapter", String.format("New child: %s", child));
                                                            } else {
                                                                Log.e("ChoresAdapter", "Child is null");
                                                            }
                                                        } else {
                                                            Log.e("ChoresAdapter", String.format("DocumentSnapshot for ID %s is null", childId));
                                                        }
                                                    } else {
                                                        Log.e("ChoresAdapter", String.format("Get child with ID %s failed", childId));
                                                    }
                                                }
                                        );
                                        dbReference.getCollectionReference().document(chore.getId()).set(chore);
                                    }}).setActionTextColor(v.getResources().getColor(android.R.color.holo_green_light ))
                                .show();;
                    });

                    holder.undo.setOnClickListener(v -> {
                        chore.setComplete(false);
                        chore.setCompletedTimestamp(Integer.MAX_VALUE);
                        dbReference.getCollectionReference().document(chore.getId()).set(chore);
                    });


                } else {
                    holder.dateText.setText(String.format("Approved %s ago", getDateToStringProcessor(chore.getApprovedTimestamp())));
                    holder.delete.setVisibility(View.INVISIBLE);
                    holder.edit.setVisibility(View.INVISIBLE);
                    holder.taskCompleted.setVisibility(View.INVISIBLE);
                    holder.taskCardview.setCardBackgroundColor(Color.LTGRAY);
                    holder.undo.setVisibility(View.INVISIBLE);

                }
            }


        }


    }

    @Override
    public int getItemCount() {
        return chores.size();
    }

    public String getDateToStringProcessor(long dueDate) {
        return getDateToStringProcessor(dueDate, System.currentTimeMillis());
    }

    public String getDateToStringProcessor(long dueDate, long currentDate) {
        String[] formatDate = new String[]{"month", "day", "hour", "minute", "second"};
        String[] formatDatePlural = new String[]{"months", "days", "hours", "minutes", "seconds"};
        SimpleDateFormat formatter = new SimpleDateFormat("MM:dd:HH:mm:ss", Locale.US);
        String[] dueDateFormat = formatter.format(new Date(dueDate)).split(":");
        String[] currentDateFormat = formatter.format(new Date(currentDate)).split(":");

        for (int i = 0; i < dueDateFormat.length; i++) {
            int time = Math.abs(Integer.parseInt(dueDateFormat[i]) - Integer.parseInt(currentDateFormat[i]));
            if (time == 1) {
                return (String.format("%s %s", time, formatDate[i]));
            } else if (time > 1) {
                return (String.format("%s %s", time, formatDatePlural[i]));
            }
        }
        return "a few seconds";
    }
}
