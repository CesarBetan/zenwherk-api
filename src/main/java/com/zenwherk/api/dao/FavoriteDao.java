package com.zenwherk.api.dao;

import com.zenwherk.api.domain.Favorite;
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
public class FavoriteDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(FavoriteDao.class);

    public Optional<Favorite> getByUuid(String uuid) {
        String sql = "SELECT * FROM favorite WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Favorite> rowMapper = new BeanPropertyRowMapper<>(Favorite.class);
            Favorite favorite = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting favorite by uuid " + uuid);
            return Optional.of(favorite);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Favorite> getByUserIdAndPlaceId(Long userId, Long placeId) {
        String sql = "SELECT * FROM favorite WHERE user_id = ? AND place_id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Favorite> rowMapper = new BeanPropertyRowMapper<>(Favorite.class);
            Favorite favorite = jdbcTemplate.queryForObject(sql, rowMapper, userId, placeId);
            logger.debug("Getting favorite by userId and placeId");
            return Optional.of(favorite);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Favorite> insert(Favorite favorite) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO favorite (uuid, status, created_at, " +
                "updated_at, user_id, place_id) " +
                "VALUE (?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, favorite.getStatus(), Timestamp.from(Instant.now()),
                    Timestamp.from(Instant.now()), favorite.getUserId(), favorite.getPlaceId());
            logger.debug(String.format("Creating favorite: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    private Favorite[] toFavoriteArray(List<Map<String, Object>> rows) {
        LinkedList<Favorite> favoritesList = new LinkedList<>();
        for(Map<String, Object> row : rows) {
            Favorite favorite = new Favorite();

            favorite.setId(new Long((Integer) row.get("id")));
            favorite.setUuid((String) row.get("uuid"));
            favorite.setStatus((Integer) row.get("status"));
            favorite.setCreatedAt((Date) row.get("created_at"));
            favorite.setUpdatedAt((Date) row.get("updated_at"));
            favorite.setUserId(new Long((Integer) row.get("user_id")));
            favorite.setPlaceId(new Long((Integer) row.get("place_id")));

            favoritesList.add(favorite);
        }
        return favoritesList.toArray(new Favorite[favoritesList.size()]);
    }

    public Optional<Favorite[]> getFavoritesByUserId(Long userId) {
        String sql = "SELECT * FROM favorite WHERE user_id=? AND status > 0";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
            Favorite[] favorites = toFavoriteArray(rows);
            logger.debug("Getting all favorites for user id: " + userId);
            return Optional.of(favorites);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public boolean deleteByUserIdAndPlaceId(Long userId, Long placeId) {
        String sql = "DELETE FROM favorite WHERE user_id=? AND place_id=?";
        try {
            jdbcTemplate.update(sql, userId, placeId);
            logger.debug("Deleting favorite by user id and place id");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
