package com.example.aboubacrineseckprojet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class Utils {

    public static void saveLoginSharedPreferences(Context c, String email, String password){
        System.out.println("saveLoginSharedPreferences");
        SharedPreferences.Editor editor = c.getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit();
        editor.putString("email",email);
        editor.putString("password",password);
        editor.commit();
    }
    public static SharedPreferences getSharedPreferences(Context c){
        return c.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
    }
    public static byte[] bitmapToBytes(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,50,stream);
        return stream.toByteArray();
    }
    public static Bitmap bytesToBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }
    public static void shareProfile(Intent intent,Profile profile){
        intent.putExtra("profile",profile.toStrings());
        intent.putExtra("profile_image",profile.image);
    }
    public static Profile getSharedProfileOrElseLogin(Activity c){
        Profile profile = getSharedProfile(c.getIntent());
        if (profile == null){
            c.startActivity(new Intent(c,MainActivity.class));
            c.finish();
        }
        return profile;
    }
    public static Profile getSharedProfile(Intent intent){
        String[] s = intent.getStringArrayExtra("profile");
        Profile profile = null;
        int i = -1;
        String id = s[++i];
        String name = s[++i];
        String email = s[++i];
        String password = s[++i];
        String phone = s[++i];
        boolean male = Boolean.parseBoolean(s[++i]);
        String major = s[++i];
        byte[] image = intent.getByteArrayExtra("profile_image");
        if(
            name != null &&
            email != null &&
            password != null &&
            phone != null &&
            major != null &&
            image != null
        ){
             profile = new Profile(Long.parseLong(id),name, email,password, image, phone,male,major);
        }
        return profile;
    }
    public static Profile loadProfile(Context c,ProfileDBHelper db){
        System.out.println("loadProfile");
        SharedPreferences editor = Utils.getSharedPreferences(c);
        String email = editor.getString("email",null);
        String password = editor.getString("password",null);
        if(email == null || password == null){
            return null;
        }
        return db.getProfileFromDB(email,password);
    }
    public static void hideKeyboard(Activity c,View v){
        ((InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    public static void startActivityAndShareProfile(Activity c,Profile profile,Class cls,boolean finish) {
        System.out.println("startActivityAndShareProfile");
        saveLoginSharedPreferences(c,profile.email,profile.password);
        Intent intent = new Intent(c,cls);
        shareProfile(intent,profile);
        c.startActivity(intent);
        if (finish){
            c.finish();
        }
    }
    public static void showSnackBar(View parent,int stringId){
        System.out.println("showSnackBar");
        final Snackbar alertMessage = Snackbar.make(parent,stringId,Snackbar.LENGTH_INDEFINITE);
        alertMessage.setAction("OK",(v)-> alertMessage.dismiss());
        alertMessage.show();
    }
}
