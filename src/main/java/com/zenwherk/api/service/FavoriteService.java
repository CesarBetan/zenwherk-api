package com.zenwherk.api.service;

import com.zenwherk.api.dao.FavoriteDao;
import com.zenwherk.api.domain.Favorite;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.FavoriteValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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

    public ListResult<Place> getFavoritePlacesByUserId(Long userId) {
        ListResult<Place> result = new ListResult<>();

        Optional<Favorite[]> favorites = favoriteDao.getFavoritesByUserId(userId);
        if(!favorites.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        LinkedList<Place> places = new LinkedList<>();
        for(Favorite favorite : favorites.get()) {
            Result<Place> placeResult = placeService.getPlaceById(favorite.getPlaceId(), true, false);
            if(placeResult.getData().isPresent()) {
                places.add(placeResult.getData().get());
            }
        }

        result.setData(Optional.of(places.toArray(new Place[places.size()])));
        return result;
    }

    public Result<Favorite> insert(Favorite favorite) {
        Result<Favorite> result = FavoriteValidation.validateInsert(favorite);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        favorite.setStatus(1);

        // Retrieve the user that uploaded the favorite
        Result<User> userResult = userService.getUserByUuid(favorite.getUser().getUuid(), true, true);
        if(!userResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este favorito no es válido"));
            return result;
        }

        // Retrieve the place to which this favorite belongs to
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

    public MessageResult deleteByUserUuidAndPlaceUuid(Favorite favorite) {
        MessageResult result = FavoriteValidation.validateInsert(favorite);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        // Retrieve the user that uploaded the favorite
        Result<User> userResult = userService.getUserByUuid(favorite.getUser().getUuid(), true, true);
        if(!userResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este favorito no es válido"));
            return result;
        }

        // Retrieve the place to which this favorite belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(favorite.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de este favorito no es válido"));
            return result;
        }

        boolean deleted = favoriteDao.deleteByUserIdAndPlaceId(userResult.getData().get().getId(), placeResult.getData().get().getId());

        if(deleted) {
            result.setMessage(new Message("Favorito borrado correctamente"));
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
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
