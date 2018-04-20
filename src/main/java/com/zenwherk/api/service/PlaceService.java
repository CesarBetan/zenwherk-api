package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceFeatureService placeFeatureService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceService.class);

    public Result<Place> getPlaceById(Long id, boolean keepId, boolean keepStatus) {
        Result<Place> result = new Result<>();

        Optional<Place> place = placeDao.getById(id);
        if(place.isPresent()){
            Long userId = place.get().getUploadedBy();
            place = Optional.of(cleanPlaceFields(place.get(), keepId, keepStatus));

            Result<User> uploadedBy = userService.getUserById(userId, false, false);
            if(uploadedBy.getData().isPresent() && place.isPresent()) {
                place.get().setUser(uploadedBy.getData().get());
            }
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
        }
        result.setData(place);
        return result;
    }

    public Result<Place> getPlaceByUuid(String uuid, boolean keepId, boolean keepStatus) {
        Result<Place> result = new Result<>();

        Optional<Place> place = placeDao.getByUuid(uuid);
        if(place.isPresent()){
            Long userId = place.get().getUploadedBy();
            place = Optional.of(cleanPlaceFields(place.get(), true, keepStatus));

            if(place.isPresent()) {
                // Get the user that uploaded the place
                Result<User> uploadedBy = userService.getUserById(userId, false, false);
                if(uploadedBy.getData().isPresent()) {
                    place.get().setUser(uploadedBy.getData().get());
                }

                // Get the features of this place
                place.get().setFeatures(new PlaceFeature[0]);
                ListResult<PlaceFeature> placeFeatures = placeFeatureService.getApprovedFeaturesByPlaceId(place.get().getId(), false);
                if(placeFeatures.getData().isPresent()) {
                    place.get().setFeatures(placeFeatures.getData().get());
                }
            }

        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
        }
        result.setData(place);
        return result;
    }

    public Result<Place> insert(Place place) {
        Result<Place> result = PlaceValidation.validateInsert(place);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        place.setName(place.getName().trim());
        place.setAddress(place.getAddress().trim());
        place.setDescription(place.getDescription().trim());
        place.setPhone(place.getPhone().trim());
        place.setWebsite(place.getWebsite().trim());

        Result<User> uploadedByResult = userService.getUserByUuid(place.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no es válido"));
            return result;
        }

        place.setUploadedBy(uploadedByResult.getData().get().getId());

        // If the user is an admin, the place is approved, if not it goes to an to approve status
        // Status 0: Deleted
        // Status 1: Approved
        // Status 2: To be approved
        // Status 3: To be approved - Delete
        // Role 1: Admin
        // Role 2: User
        place.setStatus(uploadedByResult.getData().get().getRole());

        Optional<Place> insertedPlace = placeDao.insert(place);
        if(insertedPlace.isPresent()) {
            insertedPlace = Optional.of(cleanPlaceFields(insertedPlace.get(), false, false));
        }

        result.setData(insertedPlace);
        return result;
    }

    private Place cleanPlaceFields(Place place, boolean keepId, boolean keepStatus) {
        if(!keepId) {
            place.setId(null);
        }
        if(!keepStatus) {
            place.setStatus(null);
        }
        place.setUploadedBy(null);
        place.setUser(null);
        return place;
    }
}