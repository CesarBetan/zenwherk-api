package com.zenwherk.api.service;

import com.zenwherk.api.dao.UserDao;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Optional<User> getUserByUuid(String uuid) {
        Optional<User> user = userDao.getByUuid(uuid);
        if(user.isPresent()){
            user.get().setPasswordHash(null);
        }
        return user;
    }

    public Result<User> insert(User user) {
        // por ejemplo query para validar que el correo no exista
        // result.setMessage("El correo ya existe");
        Result<User> result = new Result<>();
        if(user == null) {
            result.setErrorCode(2);
            result.setMessage("El cuerpo del post no puede ser nulo");
            return result;
        }

        if(user.getEmail() == null) {
            result.setErrorCode(1);
            result.setMessage("El correo no puede ser nulo");
            return result;
        }

        if(user.getPassword_hash() == null) {
            result.setErrorCode(1);
            result.setMessage("El correo no puede ser nulo");
            return result;
        }

        user.setStatus(1);
        user.setRole(2);

        // encryptar la contraseña

        // todas mis validaciones

        Optional<User> insertedUser = userDao.insert(user);
        result.setData(insertedUser);
        return result;
    }
}
