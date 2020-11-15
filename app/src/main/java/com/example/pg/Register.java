package com.example.pg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class

Register extends AppCompatActivity {

    Button registerAsParent, registerAsSpecialist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerAsParent= findViewById(R.id.register_as_a_Parent);

        registerAsSpecialist = findViewById(R.id.register_as_specialist);

        registerAsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this , RegisterUserActivity.class));
            }
        });

        registerAsSpecialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this , RegisterSpecialListActivity.class));
            }
        });

    }
}
