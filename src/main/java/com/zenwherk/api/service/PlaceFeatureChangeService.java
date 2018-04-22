package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceFeatureChangeDao;
import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.domain.PlaceFeatureChange;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceFeatureChangeValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceFeatureChangeService {

    @Autowired
    private PlaceFeatureChangeDao placeFeatureChangeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceFeatureService placeFeatureService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceFeatureChangeService.class);

    public Result<PlaceFeatureChange> insert(PlaceFeatureChange placeFeatureChange) {
        Result<PlaceFeatureChange> result = PlaceFeatureChangeValidation.validatePost(placeFeatureChange);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        placeFeatureChange.setNewFeatureDesc(placeFeatureChange.getNewFeatureDesc().trim());
        placeFeatureChange.setStatus(1);

        // Retrieve the user that uploaded the feature
        Result<User> uploadedByResult = userService.getUserByUuid(placeFeatureChange.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este cambio no es válido"));
            return result;
        }

        // Retrieve the place feature to which this feature belongs to
        Result<PlaceFeature> placeFeatureResult = placeFeatureService.getPlaceFeatureByUuid(placeFeatureChange.getPlaceFeature().getUuid(), true);
        if(!placeFeatureResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El feature de este cambio no es válido"));
            return result;
        }

        // Set the foreign keys
        placeFeatureChange.setUserId(uploadedByResult.getData().get().getId());
        placeFeatureChange.setPlaceFeatureId(placeFeatureResult.getData().get().getId());

        Optional<PlaceFeatureChange> insertedPlaceFeatureChange = placeFeatureChangeDao.insert(placeFeatureChange);
        if(insertedPlaceFeatureChange.isPresent()) {
            insertedPlaceFeatureChange = Optional.of(cleanPlaceFeatureChangeFields(insertedPlaceFeatureChange.get(), false));
        }

        result.setData(insertedPlaceFeatureChange);
        return result;
    }

    public MessageResult approveReject(String uuid, boolean approve) {
        MessageResult result = new MessageResult();

        Optional<PlaceFeatureChange> placeFeatureChange = placeFeatureChangeDao.getByUuid(uuid);
        if(!placeFeatureChange.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El cambio no es válido"));
            return result;
        }

        if(approve) {
            Result<PlaceFeature> placeFeatureResult = placeFeatureService.getPlaceFeatureById(placeFeatureChange.get().getPlaceFeatureId(), true);
            if(!placeFeatureResult.getData().isPresent()) {
                result.setErrorCode(404);
                result.setMessage(new Message("El feature no es válido"));
                return result;
            }

            PlaceFeature newPlaceFeature = placeFeatureResult.getData().get();

            // Data was found, now update the corresponding field
            newPlaceFeature.setFeatureDescription(placeFeatureChange.get().getNewFeatureDesc());

            Result<PlaceFeature> updatedPlaceFeature = placeFeatureService.update(newPlaceFeature.getUuid(), newPlaceFeature);
            if(!updatedPlaceFeature.getData().isPresent()) {
                result.setErrorCode(500);
                result.setMessage(new Message("Error de servidor"));
                return result;
            }
        }

        boolean deletedCorrectly = placeFeatureChangeDao.deleteByUuid(placeFeatureChange.get().getUuid());
        if(!deletedCorrectly) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        if(approve) {
            result.setMessage(new Message("Cambio aplicado correctamente"));
        } else {
            result.setMessage(new Message("Cambio descartado correctamente"));
        }
        return result;
    }

    private PlaceFeatureChange cleanPlaceFeatureChangeFields(PlaceFeatureChange placeFeatureChange, boolean keepId) {
        if(!keepId) {
            placeFeatureChange.setId(null);
        }
        placeFeatureChange.setStatus(null);
        placeFeatureChange.setPlaceFeature(null);
        placeFeatureChange.setUser(null);
        placeFeatureChange.setPlaceFeatureId(null);
        placeFeatureChange.setUserId(null);
        return placeFeatureChange;
    }
}
