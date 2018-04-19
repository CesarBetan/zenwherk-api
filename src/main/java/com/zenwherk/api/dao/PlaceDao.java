package com.zenwherk.api.dao;


import com.zenwherk.api.domain.Place;
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
public class PlaceDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceDao.class);

    public Optional<Place> getByUuid(String uuid) {
        String sql = "SELECT * FROM place WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Place> rowMapper = new BeanPropertyRowMapper<>(Place.class);
            Place place = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place by uuid " + uuid);
            return Optional.of(place);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Place> insert(Place place) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place (uuid, name, address, description, phone, " +
                "category, website, latitude, longitude, status, created_at, " +
                "updated_at, uploaded_by) " +
                "VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, place.getName(), place.getAddress(), place.getDescription(),
                    place.getPhone(), place.getCategory(), place.getWebsite(),
                    place.getLatitude(), place.getLongitude(), place.getStatus(), Timestamp.from(Instant.now()),
                    Timestamp.from(Instant.now()),
                    place.getUploadedBy());
            logger.debug(String.format("Creating place: %s", place.getName()));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
