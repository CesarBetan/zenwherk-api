package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceScheduleDao;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceScheduleValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceScheduleService {

    @Autowired
    private PlaceScheduleDao placeScheduleDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceScheduleService.class);

    public Result<PlaceSchedule> getPlaceScheduleById(Long id, boolean keepId) {
        Result<PlaceSchedule> result = new Result<>();

        Optional<PlaceSchedule> placeSchedule = placeScheduleDao.getById(id);
        if(placeSchedule.isPresent()){
            placeSchedule = Optional.of(cleanPlaceScheduleFields(placeSchedule.get(), keepId));
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El horario no existe"));
        }
        result.setData(placeSchedule);
        return result;
    }

    public ListResult<PlaceSchedule> getApprovedSchedulesByPlaceId(Long placeId, boolean keepId) {
        ListResult<PlaceSchedule> result = new ListResult<>();
        Optional<PlaceSchedule[]> queriedPlaceSchedules = placeScheduleDao.getApprovedPlaceSchedulesByPlaceId(placeId);
        if(queriedPlaceSchedules.isPresent()) {
            PlaceSchedule[] schedules = new PlaceSchedule[queriedPlaceSchedules.get().length];
            for(int i = 0; i < schedules.length; i++){
                schedules[i] = cleanPlaceScheduleFields(queriedPlaceSchedules.get()[i], keepId);
            }
            queriedPlaceSchedules = Optional.of(schedules);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedPlaceSchedules);
        return result;
    }

    public Result<PlaceSchedule> getPlaceScheduleByUuid(String uuid, boolean keepId) {
        Result<PlaceSchedule> result = new Result<>();

        Optional<PlaceSchedule> placeSchedule = placeScheduleDao.getByUuid(uuid);
        if(placeSchedule.isPresent()) {
            Long userId = placeSchedule.get().getUploadedBy();
            Long placeId = placeSchedule.get().getPlaceId();
            placeSchedule = Optional.of(cleanPlaceScheduleFields(placeSchedule.get(), keepId));

            Result<User> uploadedBy = userService.getUserById(userId, false, false);
            if(uploadedBy.getData().isPresent() && placeSchedule.isPresent()) {
                placeSchedule.get().setUser(uploadedBy.getData().get());
            }

            Result<Place> place = placeService.getPlaceById(placeId, false, false);
            if(place.getData().isPresent() && placeSchedule.isPresent()) {
                placeSchedule.get().setPlace(place.getData().get());
            }
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El horario no existe"));
        }

        result.setData(placeSchedule);
        return result;
    }

    public Result<PlaceSchedule> insert(PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> result = PlaceScheduleValidation.validateInsert(placeSchedule);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        // Retrieve the user that uploaded the schedule
        Result<User> uploadedByResult = userService.getUserByUuid(placeSchedule.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este horario no es válido"));
            return result;
        }

        // Retrieve the place to which this schedule belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(placeSchedule.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar de este horario no es válido"));
            return result;
        }

        // Set the foreign keys
        placeSchedule.setUploadedBy(uploadedByResult.getData().get().getId());
        placeSchedule.setPlaceId(placeResult.getData().get().getId());

        // TODO: status and day logic

        // Day logic, there should be unique day's per place id, if not, it should be a put request, not
        // an insert. The following retrieves the schedules for a place id with status 1 and 3 since they
        // are already approved
        Optional<PlaceSchedule[]> similarDays =
                placeScheduleDao.getApprovedPlaceSchedulesByDayAndPlaceId(placeSchedule.getDay(), placeSchedule.getPlaceId());
        if(similarDays.isPresent() && similarDays.get().length > 0) {
            // There are similar approved days
            result.setErrorCode(400);
            result.setMessage(new Message("Ya existe un horario para este día, favor de intentar modificar o eliminar los registros ya existentes"));
            return result;
        }

        // If the place to which this schedule belongs to has not been approved then
        // the schedule is immediately approved
        // Status 0: Deleted
        // Status 1: Approved
        // Status 2: To be approved
        // Status 3: To be approved - Delete
        if(placeResult.getData().get().getStatus() == 2) {
            placeSchedule.setStatus(1);
        } else {
            // The place to which this schedule belongs to is already approved or
            // to be deleted and that means that it is approved
            // If the user is an admin, the schedule is immediately approved, if not,
            // it should go through a change log process
            // Role 1: Admin
            // Role 2: User
            placeSchedule.setStatus(uploadedByResult.getData().get().getRole());
        }

        Optional<PlaceSchedule> insertedPlaceSchedule = placeScheduleDao.insert(placeSchedule);
        if(insertedPlaceSchedule.isPresent()) {
            insertedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(insertedPlaceSchedule.get(), false));
        }

        result.setData(insertedPlaceSchedule);
        return result;
    }

    public Result<PlaceSchedule> update(String uuid, PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> result;

        Optional<PlaceSchedule> oldPlaceSchedule = placeScheduleDao.getByUuid(uuid);
        if(!oldPlaceSchedule.isPresent()) {
            result = new Result<>();
            result.setErrorCode(404);
            result.setMessage(new Message("El horario no existe"));
            return result;
        }

        result = PlaceScheduleValidation.validateUpdate(placeSchedule);
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            return result;
        }

        PlaceSchedule newPlaceSchedule = oldPlaceSchedule.get();
        newPlaceSchedule.setOpenTime((placeSchedule.getOpenTime() != null) ? placeSchedule.getOpenTime() : newPlaceSchedule.getOpenTime());
        newPlaceSchedule.setCloseTime((placeSchedule.getCloseTime() != null) ? placeSchedule.getCloseTime() : newPlaceSchedule.getCloseTime());

        Optional<PlaceSchedule> updatedPlaceSchedule = placeScheduleDao.update(newPlaceSchedule);
        if(updatedPlaceSchedule.isPresent()) {
            updatedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(updatedPlaceSchedule.get(), false));
        }
        result.setData(updatedPlaceSchedule);
        return result;
    }

    public Result<PlaceSchedule> approveOrReject(String uuid, boolean approve) {
        Result<PlaceSchedule> result = new Result<>();

        Optional<PlaceSchedule> placeSchedule = placeScheduleDao.getByUuid(uuid);
        if(!placeSchedule.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El horario no existe"));
            return result;
        }

        // Logic
        if(approve) {
            // It is approved
            if(placeSchedule.get().getStatus() == 2) {
                // Previous schedules with the same place_id and day should be deleted
                boolean deletedPreviousSchedules =
                        placeScheduleDao.deleteByDayAndPlaceId(
                                placeSchedule.get().getDay(), placeSchedule.get().getPlaceId());
                if(deletedPreviousSchedules) {
                    placeSchedule.get().setStatus(1);
                } else {
                    result.setErrorCode(500);
                    result.setMessage(new Message("Error de servidor"));
                    return result;
                }
            } else if (placeSchedule.get().getStatus() == 3) {
                placeSchedule.get().setStatus(0);
            }
        } else {
            // It is rejected
            if(placeSchedule.get().getStatus() == 2) {
                placeSchedule.get().setStatus(0);
            } else if (placeSchedule.get().getStatus() == 3) {
                placeSchedule.get().setStatus(1);
            }
        }


        Optional<PlaceSchedule> updatedPlaceSchedule = placeScheduleDao.update(placeSchedule.get());
        if(updatedPlaceSchedule.isPresent()) {
            updatedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(updatedPlaceSchedule.get(), false));
        } else {
            updatedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(placeSchedule.get(), false));
        }
        result.setData(updatedPlaceSchedule);
        return result;
    }

    public Result<PlaceSchedule> deletePlaceSchedule(String uuid, User user) {
        Result<PlaceSchedule> result = new Result<>();

        // The user is the user deleting the schedule, the uuid is the schedule's uuid
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
            result.setMessage(new Message("Se debe especificar el usuario que desea eliminar el horario"));
            return result;
        }

        Optional<PlaceSchedule> placeSchedule = placeScheduleDao.getByUuid(uuid);
        if(!placeSchedule.isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El horario no existe"));
            return result;
        }

        Result<Place> placeResult = placeService.getPlaceById(placeSchedule.get().getPlaceId(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        // All the data to proceed was found
        // If the place is not approved, then delete the schedule
        // If the place is approved and the user is an admin: delete
        // If the place is approved and the user is not an admin: set status to 3
        // Status 0: Deleted
        // Status 1: Approved
        // Status 2: To be approved
        // Status 3: To be approved - Delete
        if(placeResult.getData().get().getStatus() == 2) {
            // The place is not approved
            placeSchedule.get().setStatus(0);
        } else {
            // The place is approved
            if(user.getRole() == 1) {
                // The user is an admin
                placeSchedule.get().setStatus(0);
            } else {
                // The user is not an admin
                placeSchedule.get().setStatus(3);
            }
        }
        placeSchedule.get().setUploadedBy(user.getId());

        Optional<PlaceSchedule> updatedPlaceSchedule = placeScheduleDao.update(placeSchedule.get());
        if(updatedPlaceSchedule.isPresent()) {
            updatedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(updatedPlaceSchedule.get(), false));
        } else {
            // The admin deleted it so it was not longer found, return the old one
            updatedPlaceSchedule = Optional.of(cleanPlaceScheduleFields(placeSchedule.get(), false));
        }

        result.setData(updatedPlaceSchedule);
        return result;
    }

    public ListResult<PlaceSchedule> getSchedulesToBeAddedOrEliminated() {
        ListResult<PlaceSchedule> result = new ListResult<>();
        Optional<PlaceSchedule[]> queriedPlaceSchedules = placeScheduleDao.getSchedulesToBeAddedOrDeleted();
        if(queriedPlaceSchedules.isPresent()) {
            PlaceSchedule[] schedules = new PlaceSchedule[queriedPlaceSchedules.get().length];
            for (int i = 0; i < schedules.length; i++) {
                schedules[i] = queriedPlaceSchedules.get()[i];
                schedules[i].setId(null);
                schedules[i].setUploadedBy(null);

                // Get the place to which this schedule belongs to
                Result<Place> placeResult = placeService.getPlaceById(schedules[i].getPlaceId(), true, true);
                if(placeResult.getData().isPresent()) {
                    placeResult.getData().get().setId(null);
                    placeResult.getData().get().setFeatures(null);
                    placeResult.getData().get().setRating(null);
                    placeResult.getData().get().setUser(null);
                    placeResult.getData().get().setFeatures(null);
                    placeResult.getData().get().setSchedules(null);
                    schedules[i].setPlace(placeResult.getData().get());
                }

                schedules[i].setPlaceId(null);
            }
            queriedPlaceSchedules = Optional.of(schedules);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedPlaceSchedules);
        return result;
    }

    private PlaceSchedule cleanPlaceScheduleFields(PlaceSchedule placeSchedule, boolean keepId) {
        if(!keepId) {
            placeSchedule.setId(null);
        }
        placeSchedule.setStatus(null);
        placeSchedule.setUploadedBy(null);
        placeSchedule.setPlaceId(null);
        return placeSchedule;
    }
}
