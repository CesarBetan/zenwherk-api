package com.zenwherk.api.service;

import com.zenwherk.api.dao.PictureDao;
import com.zenwherk.api.domain.Picture;
import com.zenwherk.api.domain.Place;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.util.FileUtilities;
import com.zenwherk.api.validation.PictureValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class PictureService {

    @Autowired
    private AmazonClient amazonClient;

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
            result.setMessage(new Message("La imagen no existe"));
        }
        result.setData(picture);
        return result;
    }

    public Result<Picture> insert(Picture picture) {
        Result<Picture> result = PictureValidation.validateInsert(picture);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        picture.setDescription("");
        picture.setExtension(picture.getExtension().trim());
        picture.setStatus(1);

        // Get the place to which this picture belongs to
        Result<Place> placeResult = placeService.getPlaceByUuid(picture.getPlace().getUuid(), true, true);
        if(!placeResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El lugar no es válido"));
            return result;
        }

        // Get the user that uploaded this picture
        Result<User> uploadedByResult = userService.getUserByUuid(picture.getUser().getUuid(), true, false);
        if(!uploadedByResult.getData().isPresent()) {
            result.setErrorCode(404);
            result.setMessage(new Message("El usuario no es válido"));
            return result;
        }

        // Set the foreign keys
        picture.setUploadedBy(uploadedByResult.getData().get().getId());
        picture.setPlaceId(placeResult.getData().get().getId());

        // Set the uuid
        picture.setUuid(UUID.randomUUID().toString());

        // Base 64 decoding
        File image;
        try {
            image = FileUtilities.decodeBase64(picture.getBase64(), picture.getExtension());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            result.setErrorCode(500);
            result.setMessage(new Message("Error decodificando la imagen"));
            return result;
        }

        // Upload to s3
        Optional<String> uploadedFileUrl = amazonClient.uploadFile(image, picture.getUuid());
        if(!uploadedFileUrl.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error guardando imagen"));
            return result;
        }

        // Set the image url
        picture.setUrl(uploadedFileUrl.get());

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
        picture.setDescription(null);
        picture.setStatus(null);
        picture.setPlaceId(null);
        picture.setUploadedBy(null);
        return picture;
    }
}
