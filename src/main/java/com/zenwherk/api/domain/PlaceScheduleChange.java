package com.zenwherk.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceScheduleChange extends Entity {

    private Long id;
    private String uuid;
    private String columnToChange;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date newTime;

    private Long placeScheduleId;
    private Long userId;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Other attributes used for relations, not database attributes
    private PlaceSchedule placeSchedule;
    private User user;

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

    public String getColumnToChange() {
        return columnToChange;
    }

    public void setColumnToChange(String columnToChange) {
        this.columnToChange = columnToChange;
    }

    public Date getNewTime() {
        return newTime;
    }

    public void setNewTime(Date newTime) {
        this.newTime = newTime;
    }

    public Long getPlaceScheduleId() {
        return placeScheduleId;
    }

    public void setPlaceScheduleId(Long placeScheduleId) {
        this.placeScheduleId = placeScheduleId;
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

    public PlaceSchedule getPlaceSchedule() {
        return placeSchedule;
    }

    public void setPlaceSchedule(PlaceSchedule placeSchedule) {
        this.placeSchedule = placeSchedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PlaceScheduleChange{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", columnToChange='" + columnToChange + '\'' +
                ", newTime=" + newTime +
                ", placeScheduleId=" + placeScheduleId +
                ", userId=" + userId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", placeSchedule=" + placeSchedule +
                ", user=" + user +
                '}';
    }
}
