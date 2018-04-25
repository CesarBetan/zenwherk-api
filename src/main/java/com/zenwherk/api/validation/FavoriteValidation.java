package com.zenwherk.api.validation;

import com.zenwherk.api.domain.Favorite;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class FavoriteValidation {

    public static Result<Favorite> validateInsert(Favorite favorite) {
        Result<Favorite> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(favorite == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {
            if(favorite.getPlace() == null || favorite.getPlace().getUuid() == null || favorite.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el lugar al cual pertenece este favorito. ";
            }

            if(favorite.getUser() == null || favorite.getUser().getUuid() == null || favorite.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió este favorito. ";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }
}
