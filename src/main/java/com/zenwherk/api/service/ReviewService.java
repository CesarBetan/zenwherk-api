package com.zenwherk.api.service;

import com.zenwherk.api.dao.ReviewDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.Review;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.ReviewValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    public Result<Review> insert(Review review) {
        Result<Review> result = ReviewValidation.validate(review);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        review.setReviewText(review.getReviewText().trim());
        review.setReported(0);
        review.setStatus(1);

        // Retrieve the user that uploaded the feature
        Result<User> uploadedByResult = userService.getUserByUuid(review.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de esta reseña no es válido"));
            return result;
        }

        // Retrieve the place to which this feature belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(review.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de esta reseña no es válido"));
            return result;
        }

        // Set the foreign keys
        review.setUserId(uploadedByResult.getData().get().getId());
        review.setPlaceId(placeResult.getData().get().getId());

        Optional<Review> insertedReview;

        // All data has been set, if the review already exists for this user and place id -> update
        Optional<Review> previousReview = reviewDao.getReviewByPlaceIdAndUserId(review.getPlaceId(), review.getUserId());
        if(previousReview.isPresent()) {
            // The user made a review about this place -> update it
            previousReview.get().setReviewRating(review.getReviewRating());
            previousReview.get().setReviewText(review.getReviewText());

            insertedReview = reviewDao.update(previousReview.get());
        } else {
            // Post a new review
            insertedReview = reviewDao.insert(review);
        }

        if(insertedReview.isPresent()) {
            insertedReview = Optional.of(cleanReviewFields(insertedReview.get(), false));
        }

        result.setData(insertedReview);
        return result;
    }

    private Review cleanReviewFields(Review review, boolean keepId) {
        if(!keepId) {
            review.setId(null);
        }
        review.setStatus(null);
        review.setUserId(null);
        review.setPlaceId(null);
        return review;
    }
}
