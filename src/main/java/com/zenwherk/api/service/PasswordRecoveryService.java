package com.zenwherk.api.service;

import com.zenwherk.api.dao.PasswordRecoveryTokenDao;
import com.zenwherk.api.dao.UserDao;
import com.zenwherk.api.domain.PasswordRecoveryToken;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.util.MathUtilities;
import com.zenwherk.api.validation.PasswordRecoveryTokenValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordRecoveryTokenDao passwordRecoveryTokenDao;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);

    public MessageResult generatePasswordRecoveryToken(User user) {
        MessageResult result = new MessageResult();
        if(user == null || user.getEmail() == null) {
            result.setErrorCode(400);
            result.setMessage(new Message("Favor de ingresar su correo electrónico"));
            return result;
        }


        Optional<User> userResult = userDao.getByEmail(user.getEmail());
        if(!userResult.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no existe. "));
            return result;
        }

        PasswordRecoveryToken recoveryToken = new PasswordRecoveryToken();
        recoveryToken.setToken(MathUtilities.randomDNAString(128));
        recoveryToken.setExpirationDate(Timestamp.from(Instant.now().plus(2, ChronoUnit.DAYS)));
        recoveryToken.setStatus(1);
        recoveryToken.setUserId(userResult.get().getId());

        boolean deletedPastTokens = passwordRecoveryTokenDao.deletePasswordRecoveryTokenByUserUserId(userResult.get().getId());
        Optional<PasswordRecoveryToken> insertedPasswordRecoveryToken = passwordRecoveryTokenDao.insert(recoveryToken);
        if(!deletedPastTokens || !insertedPasswordRecoveryToken.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        try {
            mailingService.sendSimpleMessage(user.getEmail(), "Recuperación de contraseña", String.format("Para cambiar su contraseña favor de ingresar a https://zenwherk-user.firebaseapp.com/recovery_token?token=%s", insertedPasswordRecoveryToken.get().getToken()) );
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        result.setMessage(new Message("Éxito! Favor de revisar su correo electrónico"));

        return result;
    }

    public MessageResult recoverPassword(PasswordRecoveryToken passwordRecoveryToken) {
        MessageResult result = PasswordRecoveryTokenValidation.validate(passwordRecoveryToken);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        Optional<PasswordRecoveryToken> queriedPasswordRecoveryToken = passwordRecoveryTokenDao.getByToken(passwordRecoveryToken.getToken().trim());
        if(!queriedPasswordRecoveryToken.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("Token inválido"));
            return result;
        }

        Result<User> userResult = userService.getUserById(queriedPasswordRecoveryToken.get().getUserId(), true, false);
        if(!userResult.getData().isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        User user = new User();
        user.setPassword(passwordRecoveryToken.getPassword());
        Result<User> updatedUser = userService.update(userResult.getData().get().getUuid(), user);
        if(!updatedUser.getData().isPresent()) {
            result.setErrorCode(updatedUser.getErrorCode());
            result.setMessage(updatedUser.getMessage());
            return result;
        }

        passwordRecoveryTokenDao.deletePasswordRecoveryTokenByUserUserId(userResult.getData().get().getId());
        result.setMessage(new Message("Su contraseña ha sido reestablecida, favor de iniciar sesión"));

        return result;
    }
}
