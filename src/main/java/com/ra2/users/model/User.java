package com.ra2.users.model;

import java.sql.Timestamp;

public class User {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String password;
    private Timestamp ultimAcces;
    private Timestamp dataCreated;
    private Timestamp dataUpdated;
    



    private String imagePath;

    public String getImagePath() { return imagePath;}

    public void setImagePath(String imagePath) {this.imagePath = imagePath;}

    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public Timestamp getUltimAcces() {
        return ultimAcces;
    }
    
    public void setUltimAcces(Timestamp ultimAcces) {
        this.ultimAcces = ultimAcces;
    }
    public Timestamp getDataCreated() {
        return dataCreated;
    }
    public void setDataCreated(Timestamp dataCreated) {
        this.dataCreated = dataCreated;
    }
    public Timestamp getDataUpdated() {
        return dataUpdated;
    }
    public void setDataUpdated(Timestamp dataUpdated) {
        this.dataUpdated = dataUpdated;
    }

  
}
