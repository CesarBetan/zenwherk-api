package com.zenwherk.api.service;

import com.zenwherk.api.dao.FavoriteDao;
import com.zenwherk.api.domain.Favorite;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.FavoriteValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    public Result<Favorite> insert(Favorite favorite) {
        Result<Favorite> result = FavoriteValidation.validateInsert(favorite);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        favorite.setStatus(1);

        // Retrieve the user that uploaded the feature
        Result<User> userResult = userService.getUserByUuid(favorite.getUser().getUuid(), true, true);
        if(!userResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este favorito no es válido"));
            return result;
        }

        // Retrieve the place to which this feature belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(favorite.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de este favorito no es válido"));
            return result;
        }

        // Set the foreign keys
        favorite.setUserId(userResult.getData().get().getId());
        favorite.setPlaceId(placeResult.getData().get().getId());

        // Check if the favorite already exists
        Optional<Favorite> existingFavorite = favoriteDao.getByUserIdAndPlaceId(favorite.getUserId(), favorite.getPlaceId());
        if(existingFavorite.isPresent()) {
            existingFavorite = Optional.of(cleanFavoriteFields(existingFavorite.get(), false));
           result.setData(existingFavorite);
        } else {
            Optional<Favorite> insertedFavorite = favoriteDao.insert(favorite);
            if(insertedFavorite.isPresent()) {
                insertedFavorite = Optional.of(cleanFavoriteFields(insertedFavorite.get(), false));
                result.setData(insertedFavorite);
            } else {
                result.setData(Optional.empty());
            }
        }

        return result;
    }

    private Favorite cleanFavoriteFields(Favorite favorite, boolean keepId) {
        if(!keepId) {
            favorite.setId(null);
        }
        favorite.setStatus(null);
        favorite.setUserId(null);
        favorite.setPlaceId(null);
        return favorite;
    }
}
