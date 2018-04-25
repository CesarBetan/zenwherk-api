package com.zenwherk.api.dao;


import com.zenwherk.api.domain.PlaceChange;
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
public class PlaceChangeDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceChangeDao.class);

    public Optional<PlaceChange> getByUuid(String uuid) {
        String sql = "SELECT * FROM place_change WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceChange> rowMapper = new BeanPropertyRowMapper<>(PlaceChange.class);
            PlaceChange placeChange = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place change by uuid " + uuid);
            return Optional.of(placeChange);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceChange> insert(PlaceChange placeChange) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place_change (uuid, column_to_change, new_value, " +
                "status, created_at, updated_at, place_id, user_id) " +
                "VALUE (?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, placeChange.getColumnToChange(), placeChange.getNewValue(),
                    placeChange.getStatus(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    placeChange.getPlaceId(), placeChange.getUserId());
            logger.debug(String.format("Creating place change: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceChange[]> getActiveChanges() {
        String sql = "SELECT * FROM place_change WHERE status > 0 AND place_id IN (SELECT place.id FROM place WHERE place.status > 0)";

        try {
            LinkedList<PlaceChange> placeChangesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            for(Map<String, Object> row : rows) {
                PlaceChange placeChange = new PlaceChange();

                placeChange.setId(new Long((Integer) row.get("id")));
                placeChange.setUuid((String) row.get("uuid"));
                placeChange.setColumnToChange((String) row.get("column_to_change"));
                placeChange.setNewValue((String) row.get("new_value"));
                placeChange.setStatus((Integer) row.get("status"));
                placeChange.setCreatedAt((Date) row.get("created_at"));
                placeChange.setUpdatedAt((Date) row.get("updated_at"));
                placeChange.setPlaceId(new Long((Integer) row.get("place_id")));
                placeChange.setUserId(new Long((Integer) row.get("user_id")));

                placeChangesList.add(placeChange);
            }
            logger.debug("Obtaining place changes to be approved or discarded");
            return Optional.of(placeChangesList.toArray(new PlaceChange[placeChangesList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public boolean deleteByUuid(String uuid) {
        String sql = "UPDATE place_change SET status=0, updated_at=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql,Timestamp.from(Instant.now()), uuid);
            logger.debug(String.format("Deleting place change with uuid: %s", uuid));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
