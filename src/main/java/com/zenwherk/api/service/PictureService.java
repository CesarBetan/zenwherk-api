package com.zenwherk.api.service;

import com.zenwherk.api.dao.PictureDao;
import com.zenwherk.api.domain.Picture;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.PictureValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PictureService {

    @Autowired
    private PictureDao pictureDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    private static final Logger logger = LoggerFactory.getLogger(PictureService.class);

    public Result<Picture> getPictureByUuid(String uuid, boolean keepId) {
        Result<Picture> result = new Result<>();

        Optional<Picture> picture = pictureDao.getByUuid(uuid);
        if(picture.isPresent()){
            picture = Optional.of(cleanPictureFields(picture.get(), keepId));
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no existe"));
        }
        result.setData(picture);
        return result;
    }

    public Result<Picture> insert(Picture picture) {
        Result<Picture> result = PictureValidation.validateInsert(picture);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        picture.setDescription(picture.getDescription().trim());
        picture.setUrl(picture.getUrl().trim());
        picture.setStatus(1);

        if(picture.getUser().getId() != null) {
            picture.setUploadedBy(picture.getUser().getId());
        } else {
            Result<User> uploadedByResult = userService.getUserByUuid(picture.getUser().getUuid(), true, false);
            if(!uploadedByResult.getData().isPresent()) {
                result.setErrorCode(404);
                result.setMessage(new Message("El usuario no es válido"));
                return result;
            }
            picture.setUploadedBy(uploadedByResult.getData().get().getId());
        }

        if(picture.getPlace().getId() != null) {
            picture.setPlaceId(picture.getPlace().getId());
        } else {
            Result<Place> placeResult = placeService.getPlaceByUuid(picture.getPlace().getUuid(), true, true);
            if(!placeResult.getData().isPresent()) {
                result.setErrorCode(404);
                result.setMessage(new Message("El lugar no es válido"));
                return result;
            }
            picture.setPlaceId(placeResult.getData().get().getId());
        }

        Optional<Picture> insertedPicture = pictureDao.insert(picture);
        if(insertedPicture.isPresent()) {
            insertedPicture = Optional.of(cleanPictureFields(insertedPicture.get(), false));
        }

        result.setData(insertedPicture);
        return result;
    }

    private Picture cleanPictureFields(Picture picture, boolean keepId) {
        if(!keepId) {
            picture.setId(null);
        }
        picture.setStatus(null);
        picture.setPlaceId(null);
        picture.setUploadedBy(null);
        picture.setPlace(null);
        picture.setUser(null);
        return picture;
    }
}