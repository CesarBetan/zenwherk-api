package com.zenwherk.api.validation;

import com.zenwherk.api.domain.PasswordRecoveryToken;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;

public class PasswordRecoveryTokenValidation {

    public static MessageResult validate(PasswordRecoveryToken passwordRecoveryToken) {
        MessageResult result = new MessageResult();
        result.setErrorCode(null);

        String message = "";

        if(passwordRecoveryToken == null) {
            result.setErrorCode(400);
            message += "El cuerpo del post no puede ser nulo. ";
        }

        if(passwordRecoveryToken.getToken() == null || passwordRecoveryToken.getToken().trim().length() < 1) {
            result.setErrorCode(400);
            message += "Token inválido. ";
        }

        if(passwordRecoveryToken.getPassword() == null || passwordRecoveryToken.getPassword().trim().length() < 8) {
            result.setErrorCode(400);
            message += "La contraseña no es válida. ";
        }

        result.setMessage(new Message(message));

        return result;
    }
}
