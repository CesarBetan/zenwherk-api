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
import java.util.Optional;
import java.util.UUID;

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
}
