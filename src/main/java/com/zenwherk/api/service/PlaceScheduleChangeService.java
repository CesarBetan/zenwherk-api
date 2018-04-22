package com.zenwherk.api.service;

import com.zenwherk.api.dao.PlaceScheduleChangeDao;
import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.domain.PlaceScheduleChange;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PlaceScheduleChangeValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaceScheduleChangeService {

    @Autowired
    private PlaceScheduleChangeDao placeScheduleChangeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceScheduleService placeScheduleService;

    private static final Logger logger = LoggerFactory.getLogger(PlaceScheduleChangeService.class);

    public Result<PlaceScheduleChange> insert(PlaceScheduleChange placeScheduleChange) {
        Result<PlaceScheduleChange> result = PlaceScheduleChangeValidation.validatePost(placeScheduleChange);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        placeScheduleChange.setColumnToChange(placeScheduleChange.getColumnToChange().trim());
        placeScheduleChange.setStatus(1);

        // Retrieve the user that uploaded the schedule
        Result<User> uploadedByResult = userService.getUserByUuid(placeScheduleChange.getUser().getUuid(), true, true);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario de este cambio no es válido"));
            return result;
        }

        // Retrieve the place schedule to which this schedule belongs to
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.getPlaceScheduleByUuid(placeScheduleChange.getPlaceSchedule().getUuid(), true);
        if(!placeScheduleResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El horario de este cambio no es válido"));
            return result;
        }

        // Set the foreign keys
        placeScheduleChange.setUserId(uploadedByResult.getData().get().getId());
        placeScheduleChange.setPlaceScheduleId(placeScheduleResult.getData().get().getId());

        Optional<PlaceScheduleChange> insertedPlaceScheduleChange = placeScheduleChangeDao.insert(placeScheduleChange);
        if(insertedPlaceScheduleChange.isPresent()) {
            insertedPlaceScheduleChange = Optional.of(cleanPlaceScheduleChangeFields(insertedPlaceScheduleChange.get(), false));
        }

        result.setData(insertedPlaceScheduleChange);
        return result;
    }

    private PlaceScheduleChange cleanPlaceScheduleChangeFields(PlaceScheduleChange placeScheduleChange, boolean keepId) {
        if(!keepId) {
            placeScheduleChange.setId(null);
        }
        placeScheduleChange.setStatus(null);
        placeScheduleChange.setPlaceSchedule(null);
        placeScheduleChange.setUser(null);
        placeScheduleChange.setPlaceScheduleId(null);
        placeScheduleChange.setUserId(null);
        return placeScheduleChange;
    }
}
