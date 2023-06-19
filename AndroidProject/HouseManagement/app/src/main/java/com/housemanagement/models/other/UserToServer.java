package com.housemanagement.models.other;

public class UserToServer {
    private String id;
    private String email;
    private String password;
    private String role;
    private Integer idFlatOwner;

    public UserToServer() {
    }

    public UserToServer(String id, String email, String password, String role, Integer idFlatOwner) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idFlatOwner = idFlatOwner;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getIdFlatOwner() {
        return idFlatOwner;
    }

    public void setIdFlatOwner(Integer idFlatOwner) {
        this.idFlatOwner = idFlatOwner;
    }
}
