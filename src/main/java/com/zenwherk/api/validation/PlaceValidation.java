package com.zenwherk.api.validation;

import com.zenwherk.api.domain.Place;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceValidation {

    public static final Pattern VALID_PHONE_REGEX =
            Pattern.compile("^[+]+[0-9]{10,13}$", Pattern.CASE_INSENSITIVE);
    public static Result<Place> validateInsert(Place place) {
        Result<Place> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(place == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(place.getName() == null || place.getName().trim().length() < 1) {
                result.setErrorCode(400);
                message += "El nombre no debe estar vacío. ";
            }

            if(place.getAddress() == null || place.getAddress().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La dirección no debe estar vacía. ";
            }

            if(place.getDescription() == null || place.getDescription().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La descripción no debe estar vacía. ";
            }

            if(place.getPhone() == null) {
                result.setErrorCode(400);
                message += "El teléfono no debe estar vacío. ";
            } else {
                Matcher matcher = VALID_PHONE_REGEX .matcher(place.getPhone());
                if(!matcher.find()) {
                    result.setErrorCode(400);
                    message += "El teléfono no es válido. ";
                }
            }

            if(place.getCategory() == null || place.getCategory() < 1) {
                result.setErrorCode(400);
                message += "La categoría no es válida. ";
            }

            if(place.getWebsite() == null) {
                result.setErrorCode(400);
                message += "La página de internet no es válida. ";
            }

            if(place.getLatitude() == null) {
                result.setErrorCode(400);
                message += "La latitud no debe estar vacía. ";
            }

            if(place.getLongitude() == null) {
                result.setErrorCode(400);
                message += "La longitud no debe estar vacía. ";
            }

            if(place.getUser() == null || place.getUser().getUuid() == null || place.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe de especificar el uuid del usuario que subío el lugar. ";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }

    public static Result<Place> validateUpdate(Place place) {
        Result<Place> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(place == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(place.getName() != null && place.getName().trim().length() < 1) {
                result.setErrorCode(400);
                message += "El nombre no debe estar vacío. ";
            }

            if(place.getAddress() != null && place.getAddress().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La dirección no debe estar vacía. ";
            }

            if(place.getDescription() != null && place.getDescription().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La descripción no debe estar vacía. ";
            }

            if(place.getPhone() != null) {
                Matcher matcher = VALID_PHONE_REGEX .matcher(place.getPhone());
                if(!matcher.find()) {
                    result.setErrorCode(400);
                    message += "El teléfono no es válido. ";
                }
            }

            if(place.getCategory() != null && place.getCategory() < 1) {
                result.setErrorCode(400);
                message += "La categoría no es válida. ";
            }
        }

        result.setMessage(new Message(message));

        return result;
    }
}
