package com.zenwherk.api.validation;

import com.zenwherk.api.domain.Picture;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

public class PictureValidation {

    public static Result<Picture> validateInsert(Picture picture) {
        Result<Picture> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(picture == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        } else {

            if(picture.getBase64() == null || picture.getBase64().trim().length() < 30) {
                result.setErrorCode(400);
                message += "La imagen no es válida. ";
            }

            if(picture.getExtension() == null || !checkValidExtension(picture.getExtension())) {
                result.setErrorCode(400);
                message += "La extensión de la imagen no es válida (Solo se aceptan JPEG y JPG. ";
            }

            if(picture.getPlace() == null || picture.getPlace().getUuid() == null || picture.getPlace().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el lugar al cual pertenece esta imagen. ";
            }

            if(picture.getUser() == null  || picture.getUser().getUuid() == null || picture.getUser().getUuid().trim().length() < 1) {
                result.setErrorCode(400);
                message += "Se debe especificar el usuario que subió la imagen. ";
            }
        }

        result.setMessage(new Message(message));
        return result;
    }

    private static boolean checkValidExtension(String extension) {
        switch (extension) {
            case "jpeg":
            case "jpg":
                return true;
        }

        return false;
    }
}
