package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class PlaceFeatureValidation {

    public static Result<PlaceFeature> validateInsert(PlaceFeature placeFeature) {
        Result<PlaceFeature> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeFeature == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {

            if(placeFeature.getFeatureDescription() == null || placeFeature.getFeatureDescription().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La descripción del feature no debe estar vacía. ";
            }

            if(placeFeature.getFeatureEnum() == null || placeFeature.getFeatureEnum() < 1) {
                result.setErrorCode(400);
                message += "El tipo de feature no es válido. ";
            }

            if(placeFeature.getPlace() == null || placeFeature.getPlace().getUuid() == null || placeFeature.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el lugar al cual pertenece este feature. ";
            }

            if(placeFeature.getUser() == null || placeFeature.getUser().getUuid() == null || placeFeature.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este feature";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }

    public static Result<PlaceFeature> validateUpdate(PlaceFeature placeFeature) {
        Result<PlaceFeature> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeFeature == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(placeFeature.getFeatureDescription() != null && placeFeature.getFeatureDescription().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La descripción del feature no debe estar vacía. ";
            }

            if(placeFeature.getFeatureEnum() != null && placeFeature.getFeatureEnum() < 1) {
                result.setErrorCode(400);
                message += "El tipo de feature no es válido. ";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
