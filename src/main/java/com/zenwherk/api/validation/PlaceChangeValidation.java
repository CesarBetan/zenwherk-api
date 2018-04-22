package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PlaceChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

import java.util.regex.Matcher;

public class PlaceChangeValidation {

    public static Result<PlaceChange> validatePost(PlaceChange placeChange) {
        Result<PlaceChange> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeChange == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(placeChange.getColumnToChange() == null || placeChange.getColumnToChange().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La columna a cambiar no debe estar vacía. ";
            } else {
                if(placeChange.getNewValue() != null) {
                    switch (placeChange.getColumnToChange()) {
                        case "name":
                        case "address":
                        case "description":
                        case "website":
                            if(placeChange.getNewValue().trim().length() < 1) {
                                result.setErrorCode(400);
                                message += "El nuevo valor no debe estar vacío. ";
                            }
                            break;
                        case "phone":
                            Matcher matcher = PlaceValidation.VALID_PHONE_REGEX.matcher(placeChange.getNewValue().trim());
                            if(!matcher.find()) {
                                result.setErrorCode(400);
                                message += "El nuevo valor debe ser un teléfono válido. ";
                            }
                            break;
                        case "category":
                            try {
                                int category = Integer.parseInt(placeChange.getNewValue().trim());
                                if(category < 1) {
                                    result.setErrorCode(400);
                                    message += "El nuevo valor debe ser una categoría válida. ";
                                }
                            } catch (Exception e) {
                                result.setErrorCode(400);
                                message += "El nuevo valor debe ser una categoría válida. ";
                            }
                            break;
                        case "latitude":
                        case "longitude":
                            try {
                                Double.parseDouble(placeChange.getNewValue().trim());
                            } catch (Exception e) {
                                result.setErrorCode(400);
                                message += "El nuevo valor debe ser una latitud/longitud válida. ";
                            }
                            break;
                        default:
                            result.setErrorCode(400);
                            message += "La columna a cambiar no es válida. ";
                            break;
                    }
                } else {
                    result.setErrorCode(400);
                    message += "El nuevo valor no debe estar vacío. ";
                }
            }

            if(placeChange.getPlace() == null || placeChange.getPlace().getUuid() == null || placeChange.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el lugar al cual pertenece este cambio. ";
            }

            if(placeChange.getUser() == null || placeChange.getUser().getUuid() == null || placeChange.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este cambio";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
