package com.zenwherk.api.service;

import com.zenwherk.api.dao.UserDao;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.UserValidation;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Result<User> getUserByUuid(String uuid, boolean keepId) {
        Result<User> result = new Result<>();

        Optional<User> user = userDao.getByUuid(uuid);
        if(user.isPresent()){
            user = Optional.of(cleanUserFields(user.get(), keepId));
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no existe"));
        }

        result.setData(user);
        return result;
    }

    public Result<User> insert(User user) {
        Result<User> result = UserValidation.validate(user);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        user.setPasswordHash(DigestUtils.sha512Hex(user.getPasswordHash()));

        user.setName(user.getName().trim());
        user.setLastName(user.getLastName().trim());
        user.setEmail(user.getEmail().toLowerCase().trim());
        user.setPicture(user.getPicture().toLowerCase().trim());
        user.setStatus(1);
        user.setRole(2);

        Optional<User> userExistsValidation = userDao.getByEmail(user.getEmail());
        if(userExistsValidation.isPresent()) {
            result.setErrorCode(400);
            result.setMessage(new Message("El usuario ya está registrado"));
            return result;
        }

        Optional<User> insertedUser = userDao.insert(user);
        if(insertedUser.isPresent()) {
            insertedUser = Optional.of(cleanUserFields(insertedUser.get(), false));
        }
        result.setData(insertedUser);
        return result;
    }

    public Result<User> update(String uuid, User user) {
        Result<User> result;
        Optional<User> oldUser = userDao.getByUuid(uuid);
        if(!oldUser.isPresent()) {
            result = new Result<>();
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no existe"));
            return result;
        }

        result = UserValidation.validatePut(user);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        User newUser = oldUser.get();
        newUser.setName((user.getName() != null) ? user.getName() : newUser.getName());
        newUser.setLastName((user.getLastName() != null) ? user.getLastName() : newUser.getLastName());
        newUser.setPicture((user.getPicture() != null) ? user.getPicture() : newUser.getPicture());

        if(user.getPasswordHash() != null) {
            user.setPasswordHash(DigestUtils.sha512Hex(user.getPasswordHash()));
            newUser.setPasswordHash(user.getPasswordHash());
        }

        Optional<User> updatedUser = userDao.update(newUser);
        if(updatedUser.isPresent()) {
            updatedUser = Optional.of(cleanUserFields(updatedUser.get(), false));
        }
        result.setData(updatedUser);
        return result;
    }

    private User cleanUserFields(User user, boolean keepId) {
        if(!keepId) {
            user.setId(null);
        }
        user.setPasswordHash(null);
        user.setRole(null);
        user.setStatus(null);
        return user;
    }
}
