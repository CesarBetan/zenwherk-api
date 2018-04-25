package com.zenwherk.api.dao;

import com.zenwherk.api.pojo.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RatingDao.class);

    public Double getRatingByPlaceId(Long placeId) {
        String sql = "SELECT AVG(review_rating) AS rating FROM review WHERE place_id=? AND status=1 GROUP BY place_id";
        try {
            BeanPropertyRowMapper<Rating> rowMapper = new BeanPropertyRowMapper<>(Rating.class);
            Rating rating = jdbcTemplate.queryForObject(sql, rowMapper, placeId);
            logger.debug("Getting rating by  placeId");
            return rating.getRating();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return -1.0;
    }
}
