package com.example.smartsavr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.smartsavr.databinding.ActivityChildHomeChoresUpcomingBinding;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChildHomeChoresUpcomingActivity extends AppCompatActivity {

    ActivityChildHomeChoresUpcomingBinding binding;

    static List<Chore>  listChoresUpcomingAll = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildHomeChoresUpcomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String childId = intent.getStringExtra(Utils.CHILD_ID);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.upcoming_chores);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Query queryChoresUpcoming = ChildHomeActivity.toDoCompletedDBReference.getCollectionReference().whereEqualTo("childID", ChildHomeActivity.childId).whereEqualTo("complete", false).orderBy("deadline", Query.Direction.ASCENDING).orderBy("assignedTimestamp",Query.Direction.DESCENDING);
        ChildHomeActivity.toDoCompletedDBReference.setQuery(queryChoresUpcoming);
        setFragment(R.id.upcomingActivities,ChoresListFragment.newInstance("childChoresToDoAll", childId));
    }

    private void setFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}