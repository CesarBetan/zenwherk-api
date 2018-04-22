package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class PlaceScheduleValidation {

    public static Result<PlaceSchedule> validateInsert(PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeSchedule == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(placeSchedule.getDay() == null || placeSchedule.getDay() < 1 || placeSchedule.getDay() > 7) {
                result.setErrorCode(400);
                message += "El día es inválido. ";
            }

            if(placeSchedule.getOpenTime() == null) {
                result.setErrorCode(400);
                message += "La hora de apertura es inválida. ";
            }

            if(placeSchedule.getCloseTime() == null) {
                result.setErrorCode(400);
                message += "La hora de cierre es inválida. ";
            }

            if(placeSchedule.getPlace() == null || placeSchedule.getPlace().getUuid() == null || placeSchedule.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el lugar al cual pertenece este horario. ";
            }

            if(placeSchedule.getUser() == null || placeSchedule.getUser().getUuid() == null || placeSchedule.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este horario. ";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }

    public static Result<PlaceSchedule> validateUpdate(PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(placeSchedule == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        }

        result.setMessage(new Message(message));
        return result;
    }
}
