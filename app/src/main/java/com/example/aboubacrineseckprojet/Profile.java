package com.example.aboubacrineseckprojet;


public class Profile {

    long id;
    String name;
    String email;
    String password;
    byte[] image;
    String phone;
    boolean male;
    String major;

    public Profile(long id, String name, String email, String password, byte[] image, String phone, boolean male, String major) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.phone = phone;
        this.male = male;
        this.major = major;
    }
    public String[] toStrings(){
        return new String[]{
                String.valueOf(id),
                name,
                email,
                password,
                phone,
                String.valueOf(male),
                major,
        };
    }
}
