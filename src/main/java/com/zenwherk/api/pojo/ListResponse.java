package com.zenwherk.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zenwherk.api.domain.Entity;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListResponse<T extends Entity> {

    private T[] result;

    public ListResponse(T[] result) {
        this.result = result;
    }

    public T[] getResult() {
        return result;
    }

    public void setResult(T[] result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ListResponse{" +
                "result=" + Arrays.toString(result) +
                '}';
    }
}
