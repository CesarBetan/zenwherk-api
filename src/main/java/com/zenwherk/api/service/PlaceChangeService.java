package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceChangeDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceChange;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
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

    public MessageResult approveReject(String uuid, boolean approve) {
        MessageResult result = new MessageResult();

        Optional<PlaceChange> placeChange = placeChangeDao.getByUuid(uuid);
        if(!placeChange.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El cambio no es válido"));
            return result;
        }

        if(approve) {
            Result<Place> placeResult = placeService.getPlaceById(placeChange.get().getPlaceId(), true, true);
            if(!placeResult.getData().isPresent()) {
                result.setErrorCode(404);
                result.setMessage(new Message("El lugar no es válido"));
                return result;
            }

            Place newPlace = placeResult.getData().get();

            // Data was found, now update the corresponding field
            switch (placeChange.get().getColumnToChange()) {
                case "name":
                    newPlace.setName(placeChange.get().getNewValue());
                    break;
                case "address":
                    newPlace.setAddress(placeChange.get().getNewValue());
                    break;
                case "description":
                    newPlace.setDescription(placeChange.get().getNewValue());
                    break;
                case "website":
                    newPlace.setWebsite(placeChange.get().getNewValue());
                    break;
                case "phone":
                    newPlace.setPhone(placeChange.get().getNewValue());
                    break;
                case "category":
                    newPlace.setCategory(Integer.parseInt(placeChange.get().getNewValue()));
                    break;
                case "latitude":
                    newPlace.setLatitude(Double.parseDouble(placeChange.get().getNewValue()));
                    break;
                case "longitude":
                    newPlace.setLongitude(Double.parseDouble(placeChange.get().getNewValue()));
                    break;
            }

            Result<Place> updatedPlace = placeService.update(newPlace.getUuid(), newPlace);
            if(!updatedPlace.getData().isPresent()) {
                result.setErrorCode(500);
                result.setMessage(new Message("Error de servidor"));
                return result;
            }
        }

        boolean deletedCorrectly = placeChangeDao.deleteByUuid(placeChange.get().getUuid());
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

    public ListResult<PlaceChange> getActiveChanges() {
        ListResult<PlaceChange> result = new ListResult<>();

        Optional<PlaceChange[]> activeChanges = placeChangeDao.getActiveChanges();
        if(activeChanges.isPresent()) {
            PlaceChange[] changes = new PlaceChange[activeChanges.get().length];
            for(int i = 0; i < changes.length; i++) {
                changes[i] = activeChanges.get()[i];
                changes[i].setId(null);
                changes[i].setUserId(null);

                // Get the place to which this place change belongs to
                Result<Place> placeResult = placeService.getPlaceById(changes[i].getPlaceId(), false, false);
                if(placeResult.getData().isPresent()) {
                    changes[i].setPlace(placeResult.getData().get());
                    changes[i].getPlace().setUser(null);
                    changes[i].getPlace().setFeatures(null);
                    changes[i].getPlace().setSchedules(null);
                    changes[i].getPlace().setRating(null);
                }

                changes[i].setPlaceId(null);
            }
            activeChanges = Optional.of(changes);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(activeChanges);
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
