package com.zenwherk.api.validation;

import com.zenwherk.api.domain.Feature;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class FeatureValidation {

    public static Result<Feature> validate(Feature feature) {
        Result<Feature> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(feature == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(feature.getName() == null || feature.getName().trim().length() < 1) {
                result.setErrorCode(400);
                message += "El nombre del feature no debe estar vacío";
            }

            if(feature.getCategory() == null) {
                result.setErrorCode(400);
                message += "La categoría del feature no debe estar vacía";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
