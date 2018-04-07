package com.zenwherk.api.service;

import com.zenwherk.api.dao.UserDao;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Result<User> getUserByUuid(String uuid) {
        Result<User> result = new Result<>();

        Optional<User> user = userDao.getByUuid(uuid);
        if(user.isPresent()){
            user.get().setPasswordHash(null);
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
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPasswordHash(passwordEncoder.encode(user.getPassword_hash()));

        user.setName(user.getName().trim());
        user.setLast_name(user.getLast_name().trim());
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
        result.setData(insertedUser);
        return result;
    }
}
