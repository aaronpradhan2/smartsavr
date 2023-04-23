package com.example.smartsavr;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsavr.databinding.FragmentChoresListBinding;


public class ChoresListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USR = "user";


    // TODO: Rename and change types of parameters
    private FragmentChoresListBinding binding;
    private RecyclerView.Adapter<ChoresViewHolder> adapter;
    Handler handler;
    private String childId;

    public ChoresListFragment() {
        // doesn't do anything special
    }

    public static ChoresListFragment newInstance(String user, String childId) {
        ChoresListFragment fragment = new ChoresListFragment();
        Bundle args = new Bundle();
        args.putString(USR, user);
        args.putString(Utils.CHILD_ID, childId);
        fragment.setArguments(args);
        return fragment;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChoresListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            Toast.makeText(requireActivity(), "The chores list screen was not populated correctly", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("GET STRING", bundle.getString(USR));

        childId = bundle.getString(Utils.CHILD_ID);

        if (getArguments().getString(USR).equals("childChoresCompleted")) {
            initializeRecyclerView1();

            ChildHomeActivity.choresCompletedDBReference.setChoresListener(ChildHomeActivity.listChoresCompleted, this.adapter, binding);
        } else if (getArguments().getString(USR).equals("childChoresToDo")) {
            initializeRecyclerView2();

            ChildHomeActivity.toDoCompletedDBReference.setChoresListener(ChildHomeActivity.listChoresToDo, this.adapter, binding);
        }
        else if (getArguments().getString(USR).equals("childChoresCompletedAll")) {
            initializeRecyclerView5();

            ChildHomeActivity.choresCompletedDBReference.setChoresListener(ChildHomeChoresCompletedActivity.listChoresCompletedAll, this.adapter, binding);
        } else if (getArguments().getString(USR).equals("childChoresToDoAll")) {
            initializeRecyclerView6();

            ChildHomeActivity.toDoCompletedDBReference.setChoresListener(ChildHomeChoresUpcomingActivity.listChoresUpcomingAll, this.adapter, binding);
        }
        else if (getArguments().getString(USR).equals("parentChoresCompleted")) {
            initializeRecyclerView3();

            ParentChildChoresActivity.choresCompletedDBReference.setChoresListener(ParentChildChoresActivity.listChoresCompleted, this.adapter, binding);
        } else if (getArguments().getString(USR).equals("parentChoresToDo")) {
            initializeRecyclerView4();

            ParentChildChoresActivity.toDoCompletedDBReference.setChoresListener(ParentChildChoresActivity.listChoresToDo, this.adapter, binding);
        }
    }

    private void initializeRecyclerView1() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ChoresAdapter(ChildHomeActivity.listChoresCompleted, requireActivity(), true, ChildHomeActivity.choresCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);
        handler = new Handler();
        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }

    private void initializeRecyclerView2() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ChoresAdapter(ChildHomeActivity.listChoresToDo, requireActivity(), true, ChildHomeActivity.toDoCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);


        handler = new Handler();
        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }

    private void initializeRecyclerView3() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ChoresAdapter(ParentChildChoresActivity.listChoresCompleted, requireActivity(), false, ParentChildChoresActivity.choresCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);
        handler = new Handler();
        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }

    private void initializeRecyclerView4() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ChoresAdapter(ParentChildChoresActivity.listChoresToDo, requireActivity(), false, ParentChildChoresActivity.toDoCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);



        handler = new Handler();
        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }

    private void initializeRecyclerView5() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChoresAdapter(ChildHomeChoresCompletedActivity.listChoresCompletedAll, requireActivity(), true, ChildHomeActivity.choresCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);
        handler = new Handler();
        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }

    private void initializeRecyclerView6() {
        RecyclerView choresRecyclerView = binding.recyclerView;
        choresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ChoresAdapter(ChildHomeChoresUpcomingActivity.listChoresUpcomingAll, requireActivity(), true, ChildHomeActivity.toDoCompletedDBReference, childId);
        // Associates the adapter with the RecyclerView
        choresRecyclerView.setAdapter(adapter);


        handler = new Handler();


        Runnable newRunnable = new ChoresPoller(adapter, false, handler);
        new Thread(newRunnable).start();
    }
}