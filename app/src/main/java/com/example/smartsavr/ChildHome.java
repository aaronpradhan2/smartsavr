package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.smartsavr.databinding.ActivityChildHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChildHome extends AppCompatActivity {

    ActivityChildHomeBinding binding;
    List<Chore> chores;

    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        chores = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        for(int i = 0; i<20; i++) {
            Chore chore = new Chore(Integer.toString(i), System.currentTimeMillis(),System.currentTimeMillis()-1000000,"Wash Dishes",i,false,System.currentTimeMillis()-1000000);
            chores.add(chore);
            firebaseFirestore.collection("chores").add(chore);
        }


        setContentView(R.layout.activity_child_home);
        setFragment(R.id.fragmentCompletedActivities,new CompletedActivitiesFragment(chores));
        setFragment(R.id.fragmentUpcomingActivities,new CompletedActivitiesFragment(chores));
    }

    private void setFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }
}