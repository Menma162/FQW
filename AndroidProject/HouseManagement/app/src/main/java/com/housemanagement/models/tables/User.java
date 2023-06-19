package com.housemanagement.models.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String email;
    private String password;
    private String role;
    private Integer idFlatOwner;
    private String token;

    @Ignore
    public User(String id, String email, String password, String role, Integer idFlatOwner, String token) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idFlatOwner = idFlatOwner;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdFlatOwner() {
        return idFlatOwner;
    }

    public void setIdFlatOwner(Integer idFlatOwner) {
        this.idFlatOwner = idFlatOwner;
    }

    public User() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
