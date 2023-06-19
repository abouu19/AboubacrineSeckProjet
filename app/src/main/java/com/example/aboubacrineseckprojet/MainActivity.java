package com.example.aboubacrineseckprojet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText emailET;
    EditText passwordET;
    ProfileDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new ProfileDBHelper(this);
        Profile profile = Utils.loadProfile(this,dbHelper);
        if(profile != null){
            Utils.startActivityAndShareProfile(this,profile,HomeActivity.class,true);
            return;
        }
        Button connecter = findViewById(R.id.button_connecter);
        connecter.setOnClickListener(this::connecter);
        Button register = findViewById(R.id.button_register);
        register.setOnClickListener((v)->{
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
        });
        emailET = findViewById(R.id.et_email);
        passwordET = findViewById(R.id.et_password);


    }
    private void connecter(View v){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        Profile profile = dbHelper.getProfileFromDB(email,password);
        if(profile != null){
            Utils.startActivityAndShareProfile(this,profile,HomeActivity.class,true);
            return;
        }
        Utils.hideKeyboard(this,v);
        Utils.showSnackBar(findViewById(R.id.main_layout),R.string.invalidCrendentials);
    }

}