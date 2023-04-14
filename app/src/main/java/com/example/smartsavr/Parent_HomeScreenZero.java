package com.example.smartsavr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Parent_HomeScreenZero extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_screen_zero);

        findViewById(R.id.add_a_child_text_view).setOnClickListener(view -> startActivity(new Intent(this, AddChildActivity.class)));
    }
}