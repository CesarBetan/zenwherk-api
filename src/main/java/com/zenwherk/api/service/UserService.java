package com.zenwherk.api.service;

import com.zenwherk.api.dao.UserDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
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

    @Autowired
    private FavoriteService favoriteService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ListResult<User> searchUsers(String query, boolean keepId, boolean keepRole) {
        ListResult<User> result = new ListResult<>();

        Optional<User[]> queriedUsers;
        if(query == null || query.trim().length() < 1) {
            queriedUsers = userDao.getAll();
        } else {
            queriedUsers = userDao.search(query);
        }
        if(queriedUsers.isPresent()) {
            User[] users = new User[queriedUsers.get().length];
            for(int i = 0; i < users.length; i++){
                users[i] = cleanUserFields(queriedUsers.get()[i], keepId, keepRole);
            }
            queriedUsers = Optional.of(users);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedUsers);
        return result;
    }


    public Result<User> getUserById(Long id, boolean keepId, boolean keepRole) {
        Result<User> result = new Result<>();

        Optional<User> user = userDao.getById(id);
        if(user.isPresent()){
            user = Optional.of(cleanUserFields(user.get(), keepId, keepRole));
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no existe"));
        }

        result.setData(user);
        return result;
    }

    public Result<User> getUserByUuid(String uuid, boolean keepId, boolean keepRole) {
        Result<User> result = new Result<>();

        Optional<User> user = userDao.getByUuid(uuid);
        if(user.isPresent()){
            Long userId = user.get().getId();
            user = Optional.of(cleanUserFields(user.get(), keepId, keepRole));

            if(user.isPresent()) {
                ListResult<Place> favoritePlaces = favoriteService.getFavoritePlacesByUserId(userId);
                if(favoritePlaces.getData().isPresent()) {
                    user.get().setFavorites(favoritePlaces.getData().get());
                }
            }
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

        user.setPassword(DigestUtils.sha512Hex(user.getPassword()));

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
            insertedUser = Optional.of(cleanUserFields(insertedUser.get(), false, false));
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

        if(user.getPassword() != null) {
            user.setPassword(DigestUtils.sha512Hex(user.getPassword()));
            newUser.setPassword(user.getPassword());
        }

        Optional<User> updatedUser = userDao.update(newUser);
        if(updatedUser.isPresent()) {
            updatedUser = Optional.of(cleanUserFields(updatedUser.get(), false, false));
        }
        result.setData(updatedUser);
        return result;
    }

    private User cleanUserFields(User user, boolean keepId, boolean keepRole) {
        if(!keepId) {
            user.setId(null);
        }
        if(!keepRole) {
            user.setRole(null);
        }
        user.setPassword(null);
        user.setStatus(null);
        return user;
    }
}
