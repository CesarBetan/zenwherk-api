package com.zenwherk.api.service;

import com.zenwherk.api.dao.FeatureDao;
import com.zenwherk.api.domain.Feature;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.validation.FeatureValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class FeatureService {

    @Autowired
    private FeatureDao featureDao;

    public ListResult<Feature> getAllFeatures(boolean keepId) {
        ListResult<Feature> result = new ListResult<>();

        Optional<Feature[]> queriedFeatures = featureDao.getAll();
        if(queriedFeatures.isPresent()) {
            Feature[] features = new Feature[queriedFeatures.get().length];
            for(int i = 0; i < features.length; i++){
                features[i] = cleanFeatureFields(queriedFeatures.get()[i], keepId);
            }
            queriedFeatures = Optional.of(features);
        } else {
            result.setErrorCode(500);
            result.setMessage(new Message("Error del servidor"));
        }

        result.setData(queriedFeatures);
        return result;
    }

    public Result<Feature> getFeatureByUuid(String uuid, boolean keepId) {
        Result<Feature> result = new Result<>();

        Optional<Feature> feature = featureDao.getByUuid(uuid);
        if(feature.isPresent()) {
            feature = Optional.of(cleanFeatureFields(feature.get(), keepId));
        } else {
            result.setErrorCode(404);
            result.setMessage(new Message("El feature no existe"));
        }

        result.setData(feature);
        return result;
    }

    public Result<Feature> insert(Feature feature) {
        feature.setCategory(0);
        Result<Feature> result = FeatureValidation.validate(feature);
        if(result.getErrorCode() != null && result.getErrorCode() > 0){
            return result;
        }

        feature.setName(feature.getName().trim());
        feature.setStatus(1);

        Optional<Feature> featureExistsValidation = featureDao.getByName(feature.getName());
        if(featureExistsValidation.isPresent()) {
            result.setErrorCode(400);
            result.setMessage(new Message("El feature ya existe"));
            return result;
        }

        Optional<Feature> insertedFeature = featureDao.insert(feature);
        if(insertedFeature.isPresent()) {
            insertedFeature = Optional.of(cleanFeatureFields(insertedFeature.get(), false));
        }

        result.setData(insertedFeature);
        return result;
    }

    private Feature cleanFeatureFields(Feature feature, boolean keepId) {
        if(!keepId) {
            feature.setId(null);
        }
        feature.setStatus(null);
        return feature;
    }
}
