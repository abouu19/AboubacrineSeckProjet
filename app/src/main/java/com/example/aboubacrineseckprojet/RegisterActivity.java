package com.example.aboubacrineseckprojet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText nameET;
    EditText emailET;
    EditText passwordET;
    EditText phoneET;
    RadioGroup genderRG;
    EditText majorET;
    private ProfileDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper = new ProfileDBHelper(this);
        nameET = findViewById(R.id.name_et_register);
        emailET =  findViewById(R.id.email_et_register);
        passwordET = findViewById(R.id.password_et_register);
        phoneET = findViewById(R.id.phone_et_register);
        genderRG = findViewById(R.id.gender_rg_register);
        majorET = findViewById(R.id.major_et_register);
        ((RadioButton)findViewById(R.id.male_register)).setChecked(true);
        ((RadioButton)findViewById(R.id.female_register)).setChecked(false);
        findViewById(R.id.register_register).setOnClickListener(this::register);
    }

    private void register(View v) {
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String phone = phoneET.getText().toString();
        boolean male = genderRG.getCheckedRadioButtonId() == R.id.male_register;
        String major = majorET.getText().toString();
        if(
                !name.isEmpty()
                && !email.isEmpty()
                && !password.isEmpty()
        ){
            byte[] image = {};
            Profile profile = new Profile(-1,name, email, password, image, phone, male,major);
            if(dbHelper.saveProfile(profile)){
                Utils.startActivityAndShareProfile(this,profile,HomeActivity.class,true);
                return;
            }
        }
        Utils.hideKeyboard(this,v);
        Utils.showSnackBar(findViewById(R.id.register_layout),R.string.invalidRegisterCrendentials);
    }


}