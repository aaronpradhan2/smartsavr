package com.example.smartsavr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsavr.databinding.FragmentSettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private FragmentSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.activity_settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setClickListeners();
    }

    private void setClickListeners() {
        binding.addAChildButton.setOnClickListener(view -> startActivity(new Intent(this, AddChildActivity.class)));
    }
}
