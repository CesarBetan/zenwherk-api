package com.zenwherk.api.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceFeatureChange extends Entity {

    private Long id;
    private String uuid;
    private String newFeatureDesc;

    private Long placeFeatureId;
    private Long userId;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Other attributes used for relations, not database attributes
    private PlaceFeature placeFeature;
    private User user;

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

    public String getNewFeatureDesc() {
        return newFeatureDesc;
    }

    public void setNewFeatureDesc(String newFeatureDesc) {
        this.newFeatureDesc = newFeatureDesc;
    }

    public Long getPlaceFeatureId() {
        return placeFeatureId;
    }

    public void setPlaceFeatureId(Long placeFeatureId) {
        this.placeFeatureId = placeFeatureId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PlaceFeature getPlaceFeature() {
        return placeFeature;
    }

    public void setPlaceFeature(PlaceFeature placeFeature) {
        this.placeFeature = placeFeature;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PlaceFeatureChange{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", newFeatureDesc='" + newFeatureDesc + '\'' +
                ", placeFeatureId=" + placeFeatureId +
                ", userId=" + userId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", placeFeature=" + placeFeature +
                ", user=" + user +
                '}';
    }
}
