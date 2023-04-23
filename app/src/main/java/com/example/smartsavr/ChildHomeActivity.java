package com.example.smartsavr;

import static com.example.smartsavr.Utils.centsToDollarString;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartsavr.databinding.ActivityChildHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChildHomeActivity extends AppCompatActivity {


    static List<Chore> listChoresCompleted = new ArrayList<>();
    static List<Chore> listChoresToDo = new ArrayList<>();

    static DBReference choresCompletedDBReference;
    static DBReference toDoCompletedDBReference;

    static MyFirebaseMessagingService fcmService;

    final String TAG = "ChildHomeActivity";
    ActivityChildHomeBinding binding;

    ChoresListFragment completedActivityFragmnet;
    ChoresListFragment toDoActivityFragmnet;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;

    static String childId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.activity_home);
        }

        Intent intent = getIntent();
        childId = intent.getStringExtra(Utils.CHILD_ID);
        Log.d(TAG, String.format("Child ID IS %s", childId));

        listChoresCompleted.clear();
        listChoresToDo.clear();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("chores");


        choresCompletedDBReference = new DBReference(collectionReference, firebaseFirestore);
        toDoCompletedDBReference = new DBReference(collectionReference, firebaseFirestore);

        Query queryChoresCompleted = collectionReference.whereEqualTo("childID", childId).whereEqualTo("complete", true).orderBy("completedTimestamp", Query.Direction.DESCENDING).limit(3);
        Query queryChoresApproved = collectionReference.whereEqualTo("childID", childId).whereEqualTo("complete", true).whereEqualTo("approved", true);

        Query queryChoresCompletedAll = collectionReference.whereEqualTo("childID", childId).whereEqualTo("complete", true).orderBy("completedTimestamp", Query.Direction.DESCENDING);
        Query queryChoresToDoAll = collectionReference.whereEqualTo("childID", childId).whereEqualTo("complete", false).orderBy("deadline", Query.Direction.ASCENDING).orderBy("assignedTimestamp", Query.Direction.DESCENDING);
        choresCompletedDBReference.setChoresListenerSeeTextCompleted(queryChoresCompletedAll, binding);
        choresCompletedDBReference.setChoresListenerSeeTextUpcoming(queryChoresToDoAll, binding);
        choresCompletedDBReference.setQuery(queryChoresCompleted);
        choresCompletedDBReference.setQueryComplete(queryChoresApproved);
        choresCompletedDBReference.setApprovedListener((totalBalance, sumWeekly, sumMonthly) -> {
            this.firebaseFirestore.collection("children").document(childId).get().addOnCompleteListener(response->{
                        if(response.isComplete()){
                            DocumentSnapshot result = response.getResult();
                            binding.textTotal.setText(centsToDollarString(result.toObject(Child.class).getAccountBalanceCents()));

                        }
                    }

            );


            binding.textWeekly.setText(getResources().getString(R.string.weekly_earnings, centsToDollarString(sumWeekly)));
            binding.textMonthly.setText(getResources().getString(R.string.monthly_earnings, centsToDollarString(sumMonthly)));
        });
        Query queryChoresToDo = collectionReference.whereEqualTo("childID", childId).whereEqualTo("complete", false).orderBy("deadline", Query.Direction.ASCENDING).orderBy("assignedTimestamp", Query.Direction.DESCENDING).limit(3);

        toDoCompletedDBReference.setQuery(queryChoresToDo);

        completedActivityFragmnet = ChoresListFragment.newInstance("childChoresCompleted", childId);
        toDoActivityFragmnet = ChoresListFragment.newInstance("childChoresToDo", childId);
        setFragment(R.id.fragmentChildCompletedChores, completedActivityFragmnet);
        setFragment(R.id.fragmentChildUpcomingChores, toDoActivityFragmnet);
        if (ChildHomeActivity.listChoresCompleted.size() > 3) {
            binding.linkCompletedActivities.setVisibility(View.VISIBLE);
        } else {
            binding.linkCompletedActivities.setVisibility(View.INVISIBLE);
        }

        if (ChildHomeActivity.listChoresToDo.size() > 3) {
            binding.linkUpcomingActivities.setVisibility(View.VISIBLE);
        } else {
            binding.linkUpcomingActivities.setVisibility(View.INVISIBLE);
        }
        setListeners();

        fcmService = new MyFirebaseMessagingService();
        createNotificationChannel(childId);
        fcmService.checkNotificationPermissions(this);
        fcmService.checkToSendAddChoreNotification(childId);
        fcmService.checkToSendApprovedChoreNotification(childId);
    }


    private void setFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    private void setListeners() {
        binding.linkCompletedActivities.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildHomeChoresCompletedActivity.class);
            intent.putExtra(Utils.CHILD_ID, childId);
            startActivity(intent);
        });
        binding.linkUpcomingActivities.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildHomeChoresUpcomingActivity.class);
            intent.putExtra(Utils.CHILD_ID, childId);
            startActivity(intent);
        });
        binding.linkSeeGraphs.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildSummaryActivity.class);
            intent.putExtra(Utils.CHILD_ID, childId);
            startActivity(intent);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            myIntent.putExtra(SettingsActivity.IS_CHILD_USER, true);
            startActivity(myIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void createNotificationChannel(String channelTopic) {
        NotificationChannel channel = new NotificationChannel
                (getString(R.string.channel_id), "fcm", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Notifications for "+ channelTopic);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
