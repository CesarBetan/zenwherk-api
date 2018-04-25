package com.zenwherk.api.dao;

import com.zenwherk.api.domain.PlaceFeatureChange;
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
public class PlaceFeatureChangeDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceFeatureChangeDao.class);

    public Optional<PlaceFeatureChange> getByUuid(String uuid) {
        String sql = "SELECT * FROM place_feature_change WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceFeatureChange> rowMapper = new BeanPropertyRowMapper<>(PlaceFeatureChange.class);
            PlaceFeatureChange placeFeatureChange = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place feature change by uuid " + uuid);
            return Optional.of(placeFeatureChange);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceFeatureChange> insert(PlaceFeatureChange placeFeatureChange) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place_feature_change (uuid, new_feature_desc, status, " +
                "created_at, updated_at, place_feature_id, user_id) " +
                "VALUE (?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, placeFeatureChange.getNewFeatureDesc(),
                    placeFeatureChange.getStatus(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    placeFeatureChange.getPlaceFeatureId(), placeFeatureChange.getUserId());
            logger.debug(String.format("Creating place feature change: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceFeatureChange[]> getActiveChanges() {
        String sql = "SELECT * FROM place_feature_change WHERE status > 0 AND place_feature_id IN (SELECT place_feature.id FROM place_feature WHERE place_feature.status > 0 AND place_feature.place_id IN (SELECT place.id FROM place WHERE place.status > 0))";

        try {
            LinkedList<PlaceFeatureChange> placeFeatureChangesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                PlaceFeatureChange placeFeatureChange = new PlaceFeatureChange();

                placeFeatureChange.setId(new Long((Integer) row.get("id")));
                placeFeatureChange.setUuid((String) row.get("uuid"));
                placeFeatureChange.setNewFeatureDesc((String) row.get("new_feature_desc"));
                placeFeatureChange.setStatus((Integer) row.get("status"));
                placeFeatureChange.setCreatedAt((Date) row.get("created_at"));
                placeFeatureChange.setUpdatedAt((Date) row.get("updated_at"));
                placeFeatureChange.setPlaceFeatureId(new Long((Integer) row.get("place_feature_id")));
                placeFeatureChange.setUserId(new Long((Integer) row.get("user_id")));

                placeFeatureChangesList.add(placeFeatureChange);
            }
            logger.debug("Obtaining feature changes to be approved or discarded");
            return Optional.of(placeFeatureChangesList.toArray(new PlaceFeatureChange[placeFeatureChangesList.size()]));
        }  catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public boolean deleteByUuid(String uuid) {
        String sql = "UPDATE place_feature_change SET status=0, updated_at=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql,Timestamp.from(Instant.now()), uuid);
            logger.debug(String.format("Deleting place feature change with uuid: %s", uuid));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
