package com.zenwherk.api.service;

import com.zenwherk.api.dao.ReviewDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.Review;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
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

    public Result<Review> getReviewByUuid(String uuid, boolean keepId) {
        Result<Review> result = new Result<>();

        Optional<Review> review = reviewDao.getByUuid(uuid);
        if(review.isPresent()) {
            Long userId = review.get().getUserId();
            review = Optional.of(cleanReviewFields(review.get(), keepId));

            if(review.isPresent()) {
                // Get the user that uploaded the place
                Result<User> uploadedBy = userService.getUserById(userId, false, false);
                if(uploadedBy.getData().isPresent()) {
                    review.get().setUser(uploadedBy.getData().get());
                }
            }
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El review no existe"));
        }
        result.setData(review);
        return result;
    }

    public ListResult<Review> getReviewsByPlaceId(Long id, boolean keepId) {
        ListResult<Review> result = new ListResult<>();

        Optional<Review[]> queriedReviews = reviewDao.getReviewsByPlaceId(id);
        if(queriedReviews.isPresent()) {
            Review[] reviews = new Review[queriedReviews.get().length];
            for(int i = 0; i < reviews.length; i++) {
                Long userId = queriedReviews.get()[i].getUserId();
                Review review = cleanReviewFields(queriedReviews.get()[i], keepId);

                Result<User> uploadedBy = userService.getUserById(userId, false, false);
                if(uploadedBy.getData().isPresent()) {
                    review.setUser(uploadedBy.getData().get());
                }

                reviews[i] = review;
            }
            queriedReviews = Optional.of(reviews);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedReviews);
        return result;
    }

    public ListResult<Review> getReportedReviews() {
        ListResult<Review> result = new ListResult<>();

        Optional<Review[]> queriedReviews = reviewDao.getReportedReviews();
        if(queriedReviews.isPresent()) {
            Review[] reviews = new Review[queriedReviews.get().length];
            for(int i = 0; i < reviews.length; i++) {
                Long userId = queriedReviews.get()[i].getUserId();
                Review review = cleanReviewFields(queriedReviews.get()[i], false);

                Result<User> uploadedBy = userService.getUserById(userId, false, false);
                if(uploadedBy.getData().isPresent()) {
                    review.setUser(uploadedBy.getData().get());
                }

                reviews[i] = review;
            }
            queriedReviews = Optional.of(reviews);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedReviews);
        return result;
    }

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

    public MessageResult report(String uuid) {
        MessageResult result = new MessageResult();

        Optional<Review> review = reviewDao.getByUuid(uuid);
        if(!review.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("Review no disponible"));
            return result;
        }

        review.get().setReported(1);

        Optional<Review> updatedReview = reviewDao.update(review.get());
        if(!updatedReview.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        result.setMessage(new Message("Review reportada correctamente"));
        return result;
    }

    public MessageResult acceptRejectReportedReview(String uuid, boolean accept) {
        MessageResult result = new Result<>();

        Optional<Review> review = reviewDao.getReportedReviewByUuid(uuid);
        if(review.isPresent()) {
            if(accept) {
                review.get().setStatus(0);
            } else {
                review.get().setReported(0);
            }

            Optional<Review> updatedReview = reviewDao.update(review.get());
            if(updatedReview.isPresent()) {
                result.setMessage(new Message("Reseña descartada de manera exitosa"));
            } else {
                result.setMessage(new Message("Reseña eliminada de manera exitosa"));
            }
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El review no existe"));
        }

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
