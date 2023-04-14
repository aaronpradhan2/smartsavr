package com.example.smartsavr;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.smartsavr.databinding.FragmentAddChildBinding;

import java.util.Arrays;
import java.util.List;

public class AddChildActivity extends AppCompatActivity {

    private FragmentAddChildBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.activity_add_child);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setClickListeners();

        initializeProfilePicturesRecyclerView();
    }

    private void setClickListeners() {
        // todo: move this functionality to the child chore management screen when it's implemented
        binding.saveChildButton.setOnClickListener(view -> {
            ChoreBottomSheetDialog bottomSheet = new ChoreBottomSheetDialog();
            bottomSheet.show(getSupportFragmentManager(), ChoreBottomSheetDialog.TAG);
        });
    }

    private void initializeProfilePicturesRecyclerView() {
        ProfilePictureAdapter adapter = new ProfilePictureAdapter();
        binding.profilePictureRecyclerView.setAdapter(adapter);
        binding.profilePictureRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        List<ProfilePictureItem> profilePictures = Arrays.asList(
                new ProfilePictureItem(R.drawable.asset_1),
                new ProfilePictureItem(R.drawable.asset_2),
                new ProfilePictureItem(R.drawable.asset_3),
                new ProfilePictureItem(R.drawable.asset_4),
                new ProfilePictureItem(R.drawable.asset_5),
                new ProfilePictureItem(R.drawable.asset_6),
                new ProfilePictureItem(R.drawable.asset_7),
                new ProfilePictureItem(R.drawable.asset_8));
        adapter.submitList(profilePictures);
        adapter.notifyItemRangeInserted(0, profilePictures.size());
    }
}
