package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceFeatureDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceFeatureValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceFeatureService {

    @Autowired
    private PlaceFeatureDao placeFeatureDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceFeatureService.class);

    public Result<PlaceFeature> getPlaceFeatureByUuid(String uuid, boolean keepId) {
        Result<PlaceFeature> result = new Result<>();

        Optional<PlaceFeature> placeFeature = placeFeatureDao.getByUuid(uuid);
        if(placeFeature.isPresent()){
            Long userId = placeFeature.get().getUploadedBy();
            Long placeId = placeFeature.get().getPlaceId();
            placeFeature = Optional.of(cleanPlaceFeatureFields(placeFeature.get(), keepId));

            Result<User> uploadedBy = userService.getUserById(userId, false, false);
            if(uploadedBy.getData().isPresent() && placeFeature.isPresent()) {
                placeFeature.get().setUser(uploadedBy.getData().get());
            }

            Result<Place> place = placeService.getPlaceById(placeId, false, false);
            if(place.getData().isPresent() && placeFeature.isPresent()) {
                placeFeature.get().setPlace(place.getData().get());
            }
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
        }
        result.setData(placeFeature);
        return result;
    }

    public Result<PlaceFeature> insert(PlaceFeature placeFeature) {
        Result<PlaceFeature> result = PlaceFeatureValidation.validateInsert(placeFeature);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        placeFeature.setFeatureDescription(placeFeature.getFeatureDescription().trim());

        // Retrieve the user that uploaded the feature
        Result<User> uploadedByResult = userService.getUserByUuid(placeFeature.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este feature no es válido"));
            return result;
        }

        // Retrieve the place to which this feature belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(placeFeature.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de este feature no es válido"));
        }

        // Set the foreign keys
        placeFeature.setUploadedBy(uploadedByResult.getData().get().getId());
        placeFeature.setPlaceId(placeResult.getData().get().getId());

        // If the place to which this feature belongs to has not been approved then
        // the feature is immediately approved
        // Status 0: Deleted
        // Status 1: Approved
        // Status 2: To be approved
        // Status 3: To be approved - Delete
        if(placeResult.getData().get().getStatus() > 1) {
            placeFeature.setStatus(1);
        } else {
            // The place to which this feature belongs to is already approved.
            // If the user is an admin, the feature is immediately approved, if not,
            // it should go through a change log process
            // Role 1: Admin
            // Role 2: User
            placeFeature.setStatus(uploadedByResult.getData().get().getRole());
        }

        Optional<PlaceFeature> insertedPlaceFeature = placeFeatureDao.insert(placeFeature);
        if(insertedPlaceFeature.isPresent()) {
            insertedPlaceFeature = Optional.of(cleanPlaceFeatureFields(insertedPlaceFeature.get(), false));
        }

        result.setData(insertedPlaceFeature);
        return result;
    }

    private PlaceFeature cleanPlaceFeatureFields(PlaceFeature placeFeature, boolean keepId) {
        if(!keepId) {
            placeFeature.setId(null);
        }
        placeFeature.setStatus(null);
        placeFeature.setUploadedBy(null);
        placeFeature.setPlaceId(null);
        return placeFeature;
    }
}
