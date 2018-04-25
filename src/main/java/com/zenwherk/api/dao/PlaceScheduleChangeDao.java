package com.zenwherk.api.dao;

import com.zenwherk.api.domain.PlaceScheduleChange;
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
public class PlaceScheduleChangeDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceScheduleChangeDao.class);

    public Optional<PlaceScheduleChange> getByUuid(String uuid) {
        String sql = "SELECT * FROM place_schedule_change WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceScheduleChange> rowMapper = new BeanPropertyRowMapper<>(PlaceScheduleChange.class);
            PlaceScheduleChange placeScheduleChange = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place schedule change by uuid " + uuid);
            return Optional.of(placeScheduleChange);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceScheduleChange> insert(PlaceScheduleChange placeScheduleChange) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place_schedule_change (uuid, column_to_change, new_time, " +
                "status, created_at, updated_at, place_schedule_id, user_id) " +
                "VALUE (?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, placeScheduleChange.getColumnToChange(),
                    placeScheduleChange.getNewTime(), placeScheduleChange.getStatus(),
                    Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    placeScheduleChange.getPlaceScheduleId(), placeScheduleChange.getUserId());
            logger.debug(String.format("Creating place schedule change: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceScheduleChange[]> getActiveChanges() {
        String sql = "SELECT * FROM place_schedule_change WHERE status > 0 AND place_schedule_id IN (SELECT place_schedule.id FROM place_schedule WHERE place_schedule.status > 0 AND place_schedule.place_id IN (SELECT place.id FROM place WHERE place.status > 0))";

        try {
            LinkedList<PlaceScheduleChange> placeScheduleChangesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                PlaceScheduleChange placeScheduleChange = new PlaceScheduleChange();

                placeScheduleChange.setId(new Long((Integer) row.get("id")));
                placeScheduleChange.setUuid((String) row.get("uuid"));
                placeScheduleChange.setColumnToChange((String) row.get("column_to_change"));
                placeScheduleChange.setNewTime((Date) row.get("new_time"));
                placeScheduleChange.setStatus((Integer) row.get("status"));
                placeScheduleChange.setCreatedAt((Date) row.get("created_at"));
                placeScheduleChange.setUpdatedAt((Date) row.get("updated_at"));
                placeScheduleChange.setPlaceScheduleId(new Long((Integer) row.get("place_schedule_id")));
                placeScheduleChange.setUserId(new Long((Integer) row.get("user_id")));

                placeScheduleChangesList.add(placeScheduleChange);
            }
            logger.debug("Obtaining schedule changes to be approved or discarded");
            return Optional.of(placeScheduleChangesList.toArray(new PlaceScheduleChange[placeScheduleChangesList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public boolean deleteByUuid(String uuid) {
        String sql = "UPDATE place_schedule_change SET status=0, updated_at=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql,Timestamp.from(Instant.now()), uuid);
            logger.debug(String.format("Deleting place schedule change with uuid: %s", uuid));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
