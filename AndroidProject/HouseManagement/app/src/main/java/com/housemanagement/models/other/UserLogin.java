package com.housemanagement.models.other;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.housemanagement.interfaces.ApiRequests;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserLogin {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserLogin() {
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
