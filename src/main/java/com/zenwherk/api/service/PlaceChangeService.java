package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceChangeDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceChange;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceChangeValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceChangeService {

    @Autowired
    private PlaceChangeDao placeChangeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceChangeService.class);


    public Result<PlaceChange> insert(PlaceChange placeChange) {
        Result<PlaceChange> result = PlaceChangeValidation.validatePost(placeChange);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        placeChange.setColumnToChange(placeChange.getColumnToChange().trim());
        placeChange.setNewValue(placeChange.getNewValue().trim());
        placeChange.setStatus(1);

        // Retrieve the user that uploaded the feature
        Result<User> uploadedByResult = userService.getUserByUuid(placeChange.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este feature no es válido"));
            return result;
        }

        // Retrieve the place to which this feature belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(placeChange.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de este feature no es válido"));
            return result;
        }

        // Set the foreign keys
        placeChange.setUserId(uploadedByResult.getData().get().getId());
        placeChange.setPlaceId(placeResult.getData().get().getId());

        Optional<PlaceChange> insertedPlaceChange = placeChangeDao.insert(placeChange);
        if(insertedPlaceChange.isPresent()) {
            insertedPlaceChange = Optional.of(cleanPlaceChangeFields(insertedPlaceChange.get(), false));
        }

        result.setData(insertedPlaceChange);
        return result;
    }

    private PlaceChange cleanPlaceChangeFields(PlaceChange placeChange, boolean keepId) {
        if(!keepId) {
            placeChange.setId(null);
        }
        placeChange.setStatus(null);
        placeChange.setPlace(null);
        placeChange.setUser(null);
        placeChange.setPlaceId(null);
        placeChange.setUserId(null);
        return placeChange;
    }
}
