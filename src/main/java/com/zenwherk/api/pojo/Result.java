package com.zenwherk.api.pojo;

import com.zenwherk.api.domain.Entity;

import java.util.Optional;

public class Result<T extends Entity> {

    private Integer errorCode;
    private String message;

    private Optional<T> data;

    public Result(){
        data = Optional.empty();
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Optional<T> getData() {
        return data;
    }

    public void setData(Optional<T> data) {
        this.data = data;
    }
}
