package com.zenwherk.api.validation;

import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidation {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static Result<User> validate(User user){
        Result<User> result = new Result<>();
        result.setErrorCode(null);

        String message = "";

        if(user == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        }

        if(user.getName() == null || user.getName().trim().length() < 1){
            result.setErrorCode(400);
            message += "El nombre no debe estar vacío ";
        }

        if(user.getLast_name() == null || user.getLast_name().trim().length() < 1){
            result.setErrorCode(400);
            message += "El apellido no debe estar vacío ";
        }

        if(user.getEmail() == null) {
            result.setErrorCode(400);
            message += "El correo no debe estar vacío. ";
        } else {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(user.getEmail());
            if(!matcher.find()) {
                result.setErrorCode(400);
                message += "El correo no es válido";
            }
        }

        if(user.getPassword_hash() == null || user.getPassword_hash().trim().length() < 8) {
            result.setErrorCode(400);
            message += "La contraseña no es válida. ";
        }

        if(user.getPicture() == null) {
            result.setErrorCode(400);
            message += "La imagen no debe estar nula";
        }

        result.setMessage(new Message(message));

        return result;
    }
}
