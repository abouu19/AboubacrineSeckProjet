package com.example.aboubacrineseckprojet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SettingActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 102;
    ProfileDBHelper dbHelper;


    EditText nameET;
    EditText emailET;
    EditText passwordET;
    ImageView imageView;
    EditText phoneET;
    RadioGroup genderRG;
    EditText majorET;
    Button changeImage;
    Button saveButton;
    byte[] image;

    Profile profile;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncherCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if((profile = Utils.getSharedProfileOrElseLogin(this)) == null){
            return;
        }

        dbHelper = new ProfileDBHelper(this);
        image = profile.image;

        nameET = findViewById(R.id.name_et_settings);
        emailET =  findViewById(R.id.email_et_settings);
        passwordET = findViewById(R.id.password_et_settings);
        imageView = findViewById(R.id.image_settings);
        changeImage = findViewById(R.id.change_image_settings);
        phoneET = findViewById(R.id.phone_et_settings);
        genderRG = findViewById(R.id.gender_rg_settings);
        majorET = findViewById(R.id.major_et_settings);
        saveButton = findViewById(R.id.save_settings);
//        ((TextView) findViewById(R.id.id_settings)).setText("ID: " + profile.id);

        nameET.setText(profile.name);
        emailET.setText(profile.email);
        passwordET.setText(profile.password);
        imageView.setImageBitmap(Utils.bytesToBitmap(profile.image));
        phoneET.setText(profile.phone);
        ((RadioButton)findViewById(R.id.male_settings)).setChecked(profile.male);
        ((RadioButton)findViewById(R.id.female_settings)).setChecked(!profile.male);
        majorET.setText(profile.major);
        changeImage.setOnClickListener(this::changeImage);
        saveButton.setOnClickListener(this::update);


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result->{
                    Intent intent = result.getData();
                    if(intent == null)
                        return;
                    Uri uri = intent.getData();
                    Bitmap bitmap = null;
                    try {
                        /*
                        bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                                ,
                                bitmap.getGenerationId(),
                                MediaStore.Images.Thumbnails.MICRO_KIND,
                                null);
                         */
                        //bitmap = getContentResolver().loadThumbnail(uri,new Size);
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        //bitmap = ThumbnailUtils.extractThumbnail(bitmap,100,100);

                    } catch (IOException e) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeFile(uri.getPath(),options);
                        e.printStackTrace();
                    }
                    //imageView.setImageBitmap(imageData);
                    if(bitmap != null){
                        image = Utils.bitmapToBytes(bitmap);
                        while(image.length > 500_000){
                            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*.8),(int)(bitmap.getHeight()*.8),true);
                            image = Utils.bitmapToBytes(bitmap);
                        }
                        imageView.setImageBitmap(Utils.bytesToBitmap(image));
                    }else{
                        Toast.makeText(this, "fichier non trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        activityResultLauncherCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result->{
                    Intent intent = result.getData();
                    if(intent == null)
                        return;
                    Bundle extras = intent.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    if(bitmap != null){
                        image = Utils.bitmapToBytes(bitmap);
                        while(image.length > 500_000){
                            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*.8),(int)(bitmap.getHeight()*.8),true);
                            image = Utils.bitmapToBytes(bitmap);
                        }
                        imageView.setImageBitmap(Utils.bytesToBitmap(image));
                    }else{
                        Toast.makeText(this, "fichier non trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void update(View v){
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String phone = phoneET.getText().toString();
        boolean male = genderRG.getCheckedRadioButtonId() == R.id.male_settings;
        String major = majorET.getText().toString();
        Profile profile = new Profile(this.profile.id,name, email, password, image, phone, male,major);
        String toastMessage = dbHelper.updateProfile(profile) ? "Enregistré":"Non Enregistré";
        Utils.saveLoginSharedPreferences(this,profile.email,profile.password);
        Toast.makeText(this,toastMessage ,Toast.LENGTH_SHORT).show();
    }

    private void changeImage(View v){
        PopupMenu popupMenu = new PopupMenu(this,changeImage);
        popupMenu.getMenuInflater().inflate(R.menu.change_image_popup,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener((item)->{
            int id = item.getItemId();
            if (id == R.id.first){
                changeImageFromCamera();
            }else if (id == R.id.second ){
                changeImageFromGallery2();
            }
//            switch (id){
//                case R.id.first:
//                    changeImageFromCamera();
//                    break;
//                case R.id.second:
//                    changeImageFromGallery2();
//                    break;
//            }
            return true;
        });
        popupMenu.show();
    }
    private void changeImageFromCamera(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                                Manifest.permission.CAMERA
                                },CAMERA_PERMISSION_CODE);
            Toast.makeText(this, "Geting camera permission", Toast.LENGTH_SHORT).show();
            return;
        }
        launchCamera();
    }
    private void launchCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launchIntent(intent,true);
    }

    private void changeImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchIntent(Intent.createChooser(intent,"Select Image"),false);
    }
    private void changeImageFromGallery2(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent().setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");
        Intent chooseIntent = Intent.createChooser(getIntent,"Select Image");
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{pickIntent});
        launchIntent(chooseIntent,false);
    }

    private void launchIntent(Intent intent,boolean fromCamera) {
        if(!fromCamera)
            activityResultLauncher.launch(intent);
        else
            activityResultLauncherCamera.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera permission denied" ,Toast.LENGTH_SHORT).show();
            }else{
                launchCamera();
            }
        }
    }
}