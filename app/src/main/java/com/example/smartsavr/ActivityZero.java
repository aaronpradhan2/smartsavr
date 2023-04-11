package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.smartsavr.databinding.ActivityMainBinding;
import com.example.smartsavr.databinding.ActivityZeroBinding;



public class ActivityZero extends AppCompatActivity {


    ActivityZeroBinding binding;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityZeroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.psignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityZero.this,LoginActivity.class));
            }
        });
    }




}