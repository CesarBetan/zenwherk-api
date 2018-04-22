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
import java.util.Optional;
import java.util.UUID;

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
