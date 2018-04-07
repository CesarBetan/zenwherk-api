package com.zenwherk.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Entity {

    private Long id;
    private String uuid;
    private String name;
    private String last_name;
    private String email;
    private String password_hash;
    private String picture;
    private int role;
    private int status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date created_at;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updated_at;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPasswordHash(String passwordHash) {
        this.password_hash = passwordHash;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date createdAt) {
        this.created_at = createdAt;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updatedAt) {
        this.updated_at = updatedAt;
    }
}
