package com.zenwherk.api.pojo;

import com.zenwherk.api.domain.Entity;

import java.util.Optional;

public class Result<T extends Entity> extends MessageResult {

    private Optional<T> data;

    public Result(){
        data = Optional.empty();
    }

    public Optional<T> getData() {
        return data;
    }

    public void setData(Optional<T> data) {
        this.data = data;
    }
}
