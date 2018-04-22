package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceFeatureDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
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


    public ListResult<PlaceFeature> getApprovedFeaturesByPlaceId(Long placeId, boolean keepId) {
        ListResult<PlaceFeature> result = new ListResult<>();
        Optional<PlaceFeature[]> queriedPlaceFeatures = placeFeatureDao.getApprovedFeaturesByPlaceId(placeId);
        if(queriedPlaceFeatures.isPresent()) {
            PlaceFeature[] features = new PlaceFeature[queriedPlaceFeatures.get().length];
            for(int i = 0; i < features.length; i++){
                features[i] = cleanPlaceFeatureFields(queriedPlaceFeatures.get()[i], keepId);
            }
            queriedPlaceFeatures = Optional.of(features);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedPlaceFeatures);
        return result;
    }

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
            result.setMessage(new Message("El feature no existe"));
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
            return result;
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
        if(placeResult.getData().get().getStatus() == 2) {
            placeFeature.setStatus(1);
        } else {
            // The place to which this feature belongs to is already approved or
            // to be deleted and that means that it is approved
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

    public Result<PlaceFeature> update(String uuid, PlaceFeature placeFeature) {
        Result<PlaceFeature> result;

        Optional<PlaceFeature> oldPlaceFeature = placeFeatureDao.getByUuid(uuid);
        if(!oldPlaceFeature.isPresent()) {
            result = new Result<>();
            result.setErrorCode(404);
            result.setMessage(new Message("El feature no existe"));
            return result;
        }

        result = PlaceFeatureValidation.validateUpdate(placeFeature);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        PlaceFeature newPlaceFeature = oldPlaceFeature.get();
        newPlaceFeature.setFeatureDescription((placeFeature.getFeatureDescription() != null) ? placeFeature.getFeatureDescription() : newPlaceFeature.getFeatureDescription());
        newPlaceFeature.setFeatureEnum((placeFeature.getFeatureEnum() != null) ? placeFeature.getFeatureEnum() : newPlaceFeature.getFeatureEnum());

        Optional<PlaceFeature> updatedPlaceFeature = placeFeatureDao.update(newPlaceFeature);
        if(updatedPlaceFeature.isPresent()) {
            updatedPlaceFeature = Optional.of(cleanPlaceFeatureFields(updatedPlaceFeature.get(), false));
        }
        result.setData(updatedPlaceFeature);
        return result;
    }

    public Result<PlaceFeature> deletePlaceFeature(String uuid, User user) {
        Result<PlaceFeature> result = new Result<>();
        // The user is the user deleting the feature, the uuid is the feature's uuid
        boolean userFound = true;
        if(user == null || user.getUuid() == null || user.getUuid().trim().length() < 1) {
            userFound = false;
        } else {
            Result<User> userResult = userService.getUserByUuid(user.getUuid(), true, true);
            if(userResult.getData().isPresent()) {
                user = userResult.getData().get();
            } else {
                userFound = false;
            }
        }
        if(!userFound) {
            result.setErrorCode(404);
            result.setMessage(new Message("Se debe especificar el usuario que desea eliminar el feature"));
            return result;
        }

        Optional<PlaceFeature> placeFeature = placeFeatureDao.getByUuid(uuid);
        if(!placeFeature.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El feature no existe"));
            return result;
        }

        Result<Place> placeResult = placeService.getPlaceById(placeFeature.get().getPlaceId(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        // All the data to work was found
        // If the place is not approved, then delete the feature
        // If the place is approved and the user is an admin: delete
        // If the place is approved and the user is not an admin: set status to 3
        // Status 0: Deleted
        // Status 1: Approved
        // Status 2: To be approved
        // Status 3: To be approved - Delete
        if(placeResult.getData().get().getStatus() == 2) {
            // The place is not approved
            placeFeature.get().setStatus(0);
        } else {
            // The place is approved
            if(user.getRole() == 1) {
                // The user is an admin
                placeFeature.get().setStatus(0);
            } else {
                // The user is not an admin
                placeFeature.get().setStatus(3);
            }
        }
        placeFeature.get().setUploadedBy(user.getId());

        Optional<PlaceFeature> updatedPlaceFeature = placeFeatureDao.update(placeFeature.get());
        if(updatedPlaceFeature.isPresent()) {
            updatedPlaceFeature = Optional.of(cleanPlaceFeatureFields(updatedPlaceFeature.get(), false));
        } else {
            // The admin deleted it so it was not longer found, return the old one
            updatedPlaceFeature = Optional.of(cleanPlaceFeatureFields(placeFeature.get(), false));
        }

        result.setData(updatedPlaceFeature);
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
