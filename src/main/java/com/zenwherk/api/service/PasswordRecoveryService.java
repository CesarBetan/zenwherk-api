package com.zenwherk.api.service;

import com.zenwherk.api.dao.PasswordRecoveryTokenDao;
import com.zenwherk.api.domain.PasswordRecoveryToken;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.util.MathUtilities;
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

    public MessageResult generatePasswordRecoveryToken(String uuid) {
        MessageResult result = new MessageResult();
        Result<User> userResult = userService.getUserByUuid(uuid);
        if(!userResult.getData().isPresent()) {
            result.setErrorCode(userResult.getErrorCode());
            result.setMessage(userResult.getMessage());
            return result;
        }

        PasswordRecoveryToken recoveryToken = new PasswordRecoveryToken();
        recoveryToken.setToken(MathUtilities.randomDNAString(128));
        recoveryToken.setExpiration_date(Timestamp.from(Instant.now().plus(2, ChronoUnit.DAYS)));
        recoveryToken.setStatus(1);
        recoveryToken.setUser_id(userResult.getData().get().getId());

        boolean deletedPastTokens = passwordRecoveryTokenDao.deletePasswordRecoveryTokenByUserUserId(userResult.getData().get().getId());
        Optional<PasswordRecoveryToken> insertedPasswordRecoveryToken = passwordRecoveryTokenDao.insert(recoveryToken);
        if(!deletedPastTokens || !insertedPasswordRecoveryToken.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        result.setMessage(new Message("Éxito! Favor de revisar su correo electrónico"));

        return result;
    }
}
