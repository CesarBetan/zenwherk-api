package com.zenwherk.api.dao;

import com.zenwherk.api.domain.Review;
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
public class ReviewDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ReviewDao.class);

    public Optional<Review> getById(Long id) {
        String sql = "SELECT * FROM review WHERE id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Review> rowMapper = new BeanPropertyRowMapper<>(Review.class);
            Review review = jdbcTemplate.queryForObject(sql, rowMapper, id);
            logger.debug("Getting review by id " + id);
            return Optional.of(review);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> getByUuid(String uuid) {
        String sql = "SELECT * FROM review WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Review> rowMapper = new BeanPropertyRowMapper<>(Review.class);
            Review review = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting review by uuid " + uuid);
            return Optional.of(review);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review[]> getReviewsByPlaceId(Long placeId) {
        String sql = "SELECT * FROM review WHERE status > 0  AND place_id=? ORDER BY created_at DESC ";
        try {
            LinkedList<Review> reviewsList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, placeId);
            for(Map<String, Object> row : rows) {
                Review review = new Review();

                review.setId(new Long((Integer) row.get("id")));
                review.setUuid((String) row.get("uuid"));
                review.setReviewRating((Integer) row.get("review_rating"));
                review.setReviewText((String) row.get("review_text"));
                review.setReported((Integer) row.get("reported"));
                review.setStatus((Integer) row.get("status"));
                review.setCreatedAt((Date) row.get("created_at"));
                review.setUpdatedAt((Date) row.get("updated_at"));
                review.setUserId(new Long((Integer) row.get("user_id")));
                review.setPlaceId(new Long((Integer) row.get("place_id")));

                reviewsList.add(review);
            }
            logger.debug("Obtaining reviews by place id");
            return Optional.of(reviewsList.toArray(new Review[reviewsList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> getReviewByPlaceIdAndUserId (Long placeId, Long userId) {
        String sql = "SELECT * FROM review WHERE place_id = ? AND user_id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Review> rowMapper = new BeanPropertyRowMapper<>(Review.class);
            Review review = jdbcTemplate.queryForObject(sql, rowMapper, placeId, userId);
            logger.debug("Getting review by place and user id ");
            return Optional.of(review);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review[]> getReportedReviews() {
        String sql = "SELECT * FROM review WHERE status > 0  AND reported = 1 ORDER BY created_at DESC ";
        try {
            LinkedList<Review> reviewsList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                Review review = new Review();

                review.setId(new Long((Integer) row.get("id")));
                review.setUuid((String) row.get("uuid"));
                review.setReviewRating((Integer) row.get("review_rating"));
                review.setReviewText((String) row.get("review_text"));
                review.setReported((Integer) row.get("reported"));
                review.setStatus((Integer) row.get("status"));
                review.setCreatedAt((Date) row.get("created_at"));
                review.setUpdatedAt((Date) row.get("updated_at"));
                review.setUserId(new Long((Integer) row.get("user_id")));
                review.setPlaceId(new Long((Integer) row.get("place_id")));

                reviewsList.add(review);
            }
            logger.debug("Obtaining reported reviews");
            return Optional.of(reviewsList.toArray(new Review[reviewsList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> getReportedReviewByUuid(String uuid) {
        String sql = "SELECT * FROM review WHERE uuid = ? AND reported = 1 AND status > 0";
        try {
            BeanPropertyRowMapper<Review> rowMapper = new BeanPropertyRowMapper<>(Review.class);
            Review review = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting reported review by uuid " + uuid);
            return Optional.of(review);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> insert(Review review) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO review (uuid, review_rating, review_text, reported, status, " +
                "created_at, updated_at, user_id, place_id) " +
                "VALUE (?,?,?,?,?,?,?,?,?)";

        try {
            jdbcTemplate.update(sql, newUuid, review.getReviewRating(), review.getReviewText(),
                    review.getReported(), review.getStatus(), Timestamp.from(Instant.now()),
                    Timestamp.from(Instant.now()), review.getUserId(), review.getPlaceId());
            logger.debug(String.format("Creating review: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> update(Review review) {
        String sql = "UPDATE review SET " +
                "review_rating=?, review_text=?, reported=?, status=?, updated_at=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, review.getReviewRating(),
                    review.getReviewText(), review.getReported(),
                    review.getStatus(), Timestamp.from(Instant.now()),
                    review.getUuid());
            logger.debug(String.format("Updating review: %s", review.getUuid()));
            return getByUuid(review.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
