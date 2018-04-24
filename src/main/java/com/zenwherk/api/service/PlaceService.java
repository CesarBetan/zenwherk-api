package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceDao;
import com.zenwherk.api.domain.*;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceFeatureService placeFeatureService;

    @Autowired
    private PlaceScheduleService placeScheduleService;

    @Autowired
    private ReviewService reviewService;

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

                // Get the schedules of this place
                place.get().setSchedules(new PlaceSchedule[0]);
                ListResult<PlaceSchedule> placeSchedules = placeScheduleService.getApprovedSchedulesByPlaceId(place.get().getId(), false);
                if(placeSchedules.getData().isPresent()) {
                    place.get().setSchedules(placeSchedules.getData().get());
                }

                // Get the reviews
                place.get().setReviews(new Review[0]);
                ListResult<Review> placeReviews = reviewService.getReviewsByPlaceId(place.get().getId(), false);
                if(placeReviews.getData().isPresent()) {
                    place.get().setReviews(placeReviews.getData().get());
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

    public Result<Place> update(String uuid, Place place) {
        Result<Place> result;

        Optional<Place> oldPlace = placeDao.getByUuid(uuid);
        if(!oldPlace.isPresent()) {
            result = new Result<>();
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
            return result;
        }

        result = PlaceValidation.validateUpdate(place);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        Place newPlace = oldPlace.get();
        newPlace.setName((place.getName() != null) ? place.getName() : newPlace.getName());
        newPlace.setAddress((place.getAddress() != null) ? place.getAddress() : newPlace.getAddress());
        newPlace.setDescription((place.getDescription() != null) ? place.getDescription() : newPlace.getDescription());
        newPlace.setPhone((place.getPhone() != null) ? place.getPhone() : newPlace.getPhone());
        newPlace.setCategory((place.getCategory() != null) ? place.getCategory() : newPlace.getCategory());
        newPlace.setWebsite((place.getWebsite() != null) ? place.getWebsite() : newPlace.getWebsite());
        newPlace.setLatitude((place.getLatitude() != null) ? place.getLatitude() : newPlace.getLatitude());
        newPlace.setLongitude((place.getLongitude() != null) ? place.getLongitude() : newPlace.getLongitude());

        Optional<Place> updatedPlace = placeDao.update(newPlace);
        if(updatedPlace.isPresent()) {
            updatedPlace = Optional.of(cleanPlaceFields(updatedPlace.get(), false, false));
        }
        result.setData(updatedPlace);
        return result;
    }

    public Result<Place> approveOrReject(String uuid, boolean approve) {
        Result<Place> result = new Result<>();

        Optional<Place> place = placeDao.getByUuid(uuid);
        if(!place.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
            return result;
        }

        if(place.get().getStatus() == 2) {
            if(approve) {
                place.get().setStatus(1);
            } else {
                place.get().setStatus(0);
            }
        }

        Optional<Place> updatedPlace = placeDao.update(place.get());
        if(updatedPlace.isPresent()) {
            updatedPlace = Optional.of(cleanPlaceFields(updatedPlace.get(), false, false));
        } else {
            updatedPlace = Optional.of(cleanPlaceFields(place.get(), false, false));
        }
        result.setData(updatedPlace);

        return result;
    }

    public Result<Place> delete(String uuid) {
        Result<Place> result = new Result<>();

        Optional<Place> place = placeDao.getByUuid(uuid);
        if(!place.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
            return result;
        }

        place.get().setStatus(0);

        placeDao.update(place.get());

        result.setData(Optional.of(cleanPlaceFields(place.get(), false, false)));
        return result;
    }

    public ListResult<Place> searchPlaces(String query, List<String> categories, List<String> features, boolean keepId, boolean keepStatus) {
        ListResult<Place> result = new ListResult<>();

        boolean filterByFeatures = false;
        HashMap<String, Boolean> featuresMap = new HashMap<>();
        if(features != null && features.size() > 0) {
            filterByFeatures = true;
            for(String feature : features) {
                featuresMap.put(feature, true);
            }
        }

        Optional<Place[]> queriedPlaces = placeDao.searchApprovedPlaces(query, categories);
        if(queriedPlaces.isPresent()) {
            LinkedList<Place> filteredPlaces = new LinkedList<>();
            for(int i = 0; i < queriedPlaces.get().length; i++) {
                Place place = queriedPlaces.get()[i];
                Long placeId = place.getId();
                place = cleanPlaceFields(place, keepId, keepStatus);

                // Get the features of this place
                place.setFeatures(new PlaceFeature[0]);
                ListResult<PlaceFeature> placeFeatures = placeFeatureService.getApprovedFeaturesByPlaceId(placeId, false);
                if(placeFeatures.getData().isPresent()) {
                    place.setFeatures(placeFeatures.getData().get());
                }

                // Get the schedules of this place
                place.setSchedules(new PlaceSchedule[0]);
                ListResult<PlaceSchedule> placeSchedules = placeScheduleService.getApprovedSchedulesByPlaceId(placeId, false);
                if(placeSchedules.getData().isPresent()) {
                    place.setSchedules(placeSchedules.getData().get());
                }

                // If there was a feature filter delete this place unless it has one of the features
                if(filterByFeatures) {
                    boolean hasAtLeastOneFeature = false;

                    for(PlaceFeature placeFeature : place.getFeatures()) {
                        if(featuresMap.get(String.valueOf(placeFeature.getFeatureEnum())) != null && featuresMap.get(String.valueOf(placeFeature.getFeatureEnum()))) {
                            hasAtLeastOneFeature = true;
                        }
                    }

                    if(hasAtLeastOneFeature) {
                        filteredPlaces.add(place);
                    }
                } else {
                    filteredPlaces.add(place);
                }
            }

            queriedPlaces = Optional.of(filteredPlaces.toArray(new Place[filteredPlaces.size()]));
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedPlaces);
        return result;
    }

    public ListResult<Place> searchNearPlaces(String latitude, String longitude) {
        ListResult<Place> result = new ListResult<>();
        Double latDouble;
        Double lngDouble;
        try  {
            latDouble = Double.parseDouble(latitude);
            lngDouble = Double.parseDouble(longitude);
        } catch (Exception e) {
            result.setErrorCode(400);
            result.setMessage(new Message("Latitud y longitud inválidos"));
            return result;
        }

        Optional<Place[]> queriedPlaces = placeDao.searchNearPlaces(latDouble, lngDouble);
        if (queriedPlaces.isPresent()) {
            LinkedList<Place> cleanedPlaces = new LinkedList<>();
            for(int i = 0; i < queriedPlaces.get().length; i++) {
                Place place = queriedPlaces.get()[i];
                double distanceInKm = place.getDistanceInKm();
                place = cleanPlaceFields(place, false, false);
                place.setDistanceInKm(distanceInKm);
                cleanedPlaces.add(place);
            }
            queriedPlaces = Optional.of(cleanedPlaces.toArray(new Place[cleanedPlaces.size()]));
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedPlaces);
        return result;
    }

    private Place cleanPlaceFields(Place place, boolean keepId, boolean keepStatus) {
        if(!keepId) {
            place.setId(null);
        }
        if(!keepStatus) {
            place.setStatus(null);
        }
        place.setDistanceInKm(null);
        place.setUploadedBy(null);
        place.setUser(null);
        return place;
    }
}
