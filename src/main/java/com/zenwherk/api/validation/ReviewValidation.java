package com.zenwherk.api.validation;

import com.zenwherk.api.domain.Review;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class ReviewValidation {

    public static Result<Review> validate(Review review) {
        Result<Review> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(review == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(review.getReviewRating() == null || review.getReviewRating() < 1 || review.getReviewRating() > 5) {
                result.setErrorCode(400);
                message += "La calificación no debe estar vacía. ";
            }

            if(review.getReviewText() == null || review.getReviewText().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La reseña no debe estar vacía. ";
            }

            if(review.getPlace() == null || review.getPlace().getUuid() == null || review.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario al cual pertenece esta reseña. ";
            }

            if(review.getUser() == null || review.getUser().getUuid() == null || review.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario al cual pertenece esta reseña. ";
            }
        }

        result.setMessage(new Message(message));

        return result;
    }
}
