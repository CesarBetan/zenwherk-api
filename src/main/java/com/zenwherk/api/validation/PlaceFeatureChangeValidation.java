package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PlaceFeatureChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class PlaceFeatureChangeValidation {

    public static Result<PlaceFeatureChange> validatePost(PlaceFeatureChange placeFeatureChange) {
        Result<PlaceFeatureChange> result = new Result<>();
        result.setErrorCode(null);

        String message = "";


        if(placeFeatureChange == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(placeFeatureChange.getNewFeatureDesc() == null || placeFeatureChange.getNewFeatureDesc().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La nueva descripción no debe estar vacía. ";
            }

            if(placeFeatureChange.getPlaceFeature() == null || placeFeatureChange.getPlaceFeature().getUuid() == null || placeFeatureChange.getPlaceFeature().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el feature al cual pertenece este cambio. ";
            }

            if(placeFeatureChange.getUser() == null || placeFeatureChange.getUser().getUuid() == null || placeFeatureChange.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este cambio";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
