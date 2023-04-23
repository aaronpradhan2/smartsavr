package com.example.smartsavr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartsavr.databinding.ActivityParentChildChoresBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ParentChildChoresActivity extends AppCompatActivity {

    static List<Chore> listChoresCompleted =new ArrayList<>();
    static List<Chore> listChoresToDo = new ArrayList<>();

    static DBReference choresCompletedDBReference;
    static DBReference toDoCompletedDBReference;

    static FragmentManager fragmentManager;

    private Child child;

    ActivityParentChildChoresBinding binding;

    ChoresListFragment completedActivityFragment;
    ChoresListFragment toDoActivityFragment;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;


    private static final String TAG = "ParentChildChoresActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        fragmentManager = getSupportFragmentManager();
        child = (Child) intent.getSerializableExtra(Utils.CHILD);
        Log.d(TAG, String.format("Child is %s", child));
        listChoresCompleted.clear();
        listChoresToDo.clear();
        binding = ActivityParentChildChoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(child.getName() + "'s Chores");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("chores");
        //setContentView(R.layout.activity_parent_task_view);

        choresCompletedDBReference = new DBReference(collectionReference,firebaseFirestore);
        toDoCompletedDBReference = new DBReference(collectionReference,firebaseFirestore);

        Query queryChoresCompleted = collectionReference.whereEqualTo("childID", child.getId()).whereEqualTo("complete",true).orderBy("completedTimestamp", Query.Direction.DESCENDING);
        choresCompletedDBReference.setQuery(queryChoresCompleted);
        Query queryChoresToDo = collectionReference.whereEqualTo("childID", child.getId()).whereEqualTo("complete",false).orderBy("deadline", Query.Direction.ASCENDING).orderBy("assignedTimestamp",Query.Direction.DESCENDING);
        toDoCompletedDBReference.setQuery(queryChoresToDo);
        completedActivityFragment = ChoresListFragment.newInstance("parentChoresCompleted", child.getId());
        toDoActivityFragment = ChoresListFragment.newInstance("parentChoresToDo", child.getId());
        setFragment(R.id.fragmentParentCompletedChores, completedActivityFragment);
        setFragment(R.id.fragmentChildCompletedChores, toDoActivityFragment);
        setClickListeners();
    }

    private void setFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    private void setClickListeners() {
        binding.addTask.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ChoreBottomSheetDialog.CHILD_ID, child.getId());
            ChoreBottomSheetDialog bottomSheet = new ChoreBottomSheetDialog();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(fragmentManager, ChoreBottomSheetDialog.TAG);
        });
    }

    public static FragmentManager getSupportFragmentManagerParent(){
        return fragmentManager;
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
