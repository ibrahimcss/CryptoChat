package com.example.cryptochat;

public class User {
    private String email;
    private String passwoord;
    private String name;
    private String surname;
    private String username;

    public User() {
    }

    public User(int id, String email, String passwoord, String name, String surname, String username) {
        this.email = email;
        this.passwoord = passwoord;
        this.name = name;
        this.surname = surname;
        this.username = username;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswoord() {
        return passwoord;
    }

    public void setPasswoord(String passwoord) {
        this.passwoord = passwoord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}