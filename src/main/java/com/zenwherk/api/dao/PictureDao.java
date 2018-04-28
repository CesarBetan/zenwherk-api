package com.zenwherk.api.dao;

import com.zenwherk.api.domain.Picture;
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
public class PictureDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PictureDao.class);

    public Optional<Picture> getByUuid(String uuid) {
        String sql = "SELECT * FROM picture WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Picture> rowMapper = new BeanPropertyRowMapper<>(Picture.class);
            Picture picture = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting picture by uuid " + uuid);
            return Optional.of(picture);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Picture> insert(Picture picture) {
        String sql = "INSERT INTO picture (uuid, description, url, extension, status, created_at, " +
                "updated_at, place_id, uploaded_by) " +
                "VALUE (?,?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, picture.getUuid(), picture.getDescription(), picture.getUrl(), picture.getExtension(),
                    picture.getStatus(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    picture.getPlaceId(), picture.getUploadedBy());
            logger.debug(String.format("Creating picture: %s", picture.getUrl()));
            return getByUuid(picture.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Picture> update(Picture picture) {
        String sql = "UPDATE picture SET " +
                "status=?, updated_at=? WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, picture.getStatus(), Timestamp.from(Instant.now()), picture.getUuid());
            logger.debug(String.format("Updating picture: %s", picture.getUuid()));
            return getByUuid(picture.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
