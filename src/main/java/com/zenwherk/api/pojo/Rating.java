package com.zenwherk.api.pojo;

public class Rating {

    private Double rating;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "rating=" + rating +
                '}';
    }
}
