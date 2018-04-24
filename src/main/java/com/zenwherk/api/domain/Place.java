package com.zenwherk.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Place extends Entity {

    private Long id;
    private String uuid;
    private String name;
    private String address;
    private String description;
    private String phone;
    private Integer category;
    private String website;
    private Double latitude;
    private Double longitude;
    private Integer status;

    private Long uploadedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Other attributes used for relations, not database attributes
    private User user;
    private PlaceFeature[] features;
    private PlaceSchedule[] schedules;
    private Double distanceInKm;
    private Review[] reviews;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PlaceFeature[] getFeatures() {
        return features;
    }

    public void setFeatures(PlaceFeature[] features) {
        this.features = features;
    }

    public PlaceSchedule[] getSchedules() {
        return schedules;
    }

    public void setSchedules(PlaceSchedule[] schedules) {
        this.schedules = schedules;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", phone='" + phone + '\'' +
                ", category=" + category +
                ", website='" + website + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                ", uploadedBy=" + uploadedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                ", features=" + Arrays.toString(features) +
                ", schedules=" + Arrays.toString(schedules) +
                ", distanceInKm=" + distanceInKm +
                ", reviews=" + Arrays.toString(reviews) +
                '}';
    }
}
