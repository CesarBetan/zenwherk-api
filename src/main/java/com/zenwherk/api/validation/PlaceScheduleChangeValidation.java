package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PlaceScheduleChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class PlaceScheduleChangeValidation {

    public static Result<PlaceScheduleChange> validatePost(PlaceScheduleChange placeScheduleChange) {
        Result<PlaceScheduleChange> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeScheduleChange == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(placeScheduleChange.getColumnToChange() == null || placeScheduleChange.getColumnToChange().trim().length() < 1) {
                result.setErrorCode(400);
                message += "La columna a cambiar no debe estar vacía. ";
            } else {
                if(placeScheduleChange.getNewTime() != null) {
                    switch (placeScheduleChange.getColumnToChange()) {
                        case "open_time":
                        case "close_time":
                            // Success
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

            if(placeScheduleChange.getPlaceSchedule() == null || placeScheduleChange.getPlaceSchedule().getUuid() == null || placeScheduleChange.getPlaceSchedule().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el horario al cual pertenece este cambio. ";
            }

            if(placeScheduleChange.getUser() == null || placeScheduleChange.getUser().getUuid() == null || placeScheduleChange.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este cambio";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
