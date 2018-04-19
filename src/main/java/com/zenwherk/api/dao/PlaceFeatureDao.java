package com.zenwherk.api.dao;


import com.zenwherk.api.domain.PlaceFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlaceFeatureDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceFeatureDao.class);

    public Optional<PlaceFeature> getByUuid(String uuid) {
        String sql = "SELECT * FROM place_feature WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceFeature> rowMapper = new BeanPropertyRowMapper<>(PlaceFeature.class);
            PlaceFeature placeFeature = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place feature by uuid " + uuid);
            return Optional.of(placeFeature);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceFeature> insert(PlaceFeature placeFeature) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place_feature (uuid, feature_description, feature_enum, status, " +
                "created_at, updated_at, uploaded_by, place_id) " +
                "VALUE (?,?,?,?,?,?,?,?)";

        try {
            jdbcTemplate.update(sql, newUuid, placeFeature.getFeatureDescription(), placeFeature.getFeatureEnum(),
                    placeFeature.getStatus(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    placeFeature.getUploadedBy(), placeFeature.getPlaceId());
            logger.debug(String.format("Creating place feature: %s", placeFeature.getFeatureDescription()));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
