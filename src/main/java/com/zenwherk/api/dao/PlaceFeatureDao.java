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
import java.util.*;

@Repository
public class PlaceFeatureDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceFeatureDao.class);

    public Optional<PlaceFeature> getById(Long id) {
        String sql = "SELECT * FROM place_feature WHERE id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceFeature> rowMapper = new BeanPropertyRowMapper<>(PlaceFeature.class);
            PlaceFeature placeFeature = jdbcTemplate.queryForObject(sql, rowMapper, id);
            logger.debug("Getting place feature by id " + id);
            return Optional.of(placeFeature);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

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

    public Optional<PlaceFeature[]> getApprovedFeaturesByPlaceId(Long placeId) {
        String sql = "SELECT * FROM place_feature WHERE status IN (1,3)  AND place_id=?";
        try {
            LinkedList<PlaceFeature> placeFeaturesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, placeId);
            for(Map<String, Object> row : rows) {
                PlaceFeature placeFeature = new PlaceFeature();

                placeFeature.setId(new Long((Integer) row.get("id")));
                placeFeature.setUuid((String) row.get("uuid"));
                placeFeature.setFeatureDescription((String) row.get("feature_description"));
                placeFeature.setFeatureEnum((Integer) row.get("feature_enum"));
                placeFeature.setStatus((Integer) row.get("status"));
                placeFeature.setCreatedAt((Date) row.get("created_at"));
                placeFeature.setUpdatedAt((Date) row.get("updated_at"));
                placeFeature.setUploadedBy(new Long((Integer) row.get("uploaded_by")));
                placeFeature.setPlaceId(new Long((Integer) row.get("place_id")));

                placeFeaturesList.add(placeFeature);
            }
            logger.debug("Obtaining approved features");
            return Optional.of(placeFeaturesList.toArray(new PlaceFeature[placeFeaturesList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceFeature[]> getFeaturesToBeAddedOrDeleted() {
        String sql = "SELECT * FROM place_feature WHERE status IN (2,3) AND place_id IN (SELECT place.id FROM place WHERE place.status > 0)";
        try {
            LinkedList<PlaceFeature> placeFeaturesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                PlaceFeature placeFeature = new PlaceFeature();

                placeFeature.setId(new Long((Integer) row.get("id")));
                placeFeature.setUuid((String) row.get("uuid"));
                placeFeature.setFeatureDescription((String) row.get("feature_description"));
                placeFeature.setFeatureEnum((Integer) row.get("feature_enum"));
                placeFeature.setStatus((Integer) row.get("status"));
                placeFeature.setCreatedAt((Date) row.get("created_at"));
                placeFeature.setUpdatedAt((Date) row.get("updated_at"));
                placeFeature.setUploadedBy(new Long((Integer) row.get("uploaded_by")));
                placeFeature.setPlaceId(new Long((Integer) row.get("place_id")));

                placeFeaturesList.add(placeFeature);
            }
            logger.debug("Obtaining features to be added or deleted");
            return Optional.of(placeFeaturesList.toArray(new PlaceFeature[placeFeaturesList.size()]));
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

    public Optional<PlaceFeature> update(PlaceFeature placeFeature) {
        String sql = "UPDATE place_feature SET " +
                "feature_description=?, feature_enum=?, status=?, updated_at=?, uploaded_by=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, placeFeature.getFeatureDescription(),
                    placeFeature.getFeatureEnum(), placeFeature.getStatus(),
                    Timestamp.from(Instant.now()), placeFeature.getUploadedBy(),
                    placeFeature.getUuid());
            logger.debug(String.format("Updating place feature: %s", placeFeature.getUuid()));
            return getByUuid(placeFeature.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
