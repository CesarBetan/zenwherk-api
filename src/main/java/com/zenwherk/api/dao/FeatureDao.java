package com.zenwherk.api.dao;

import com.zenwherk.api.domain.Feature;
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
public class FeatureDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(FeatureDao.class);

    public Optional<Feature> getByName(String name) {
        String sql = "SELECT * FROM feature WHERE name LIKE ? AND status = 1";
        try {
            BeanPropertyRowMapper<Feature> rowMapper = new BeanPropertyRowMapper<>(Feature.class);
            Feature feature = jdbcTemplate.queryForObject(sql, rowMapper, name);
            logger.debug("Getting feature by name " + name);
            return Optional.of(feature);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Feature> getByUuid(String uuid) {
        String sql = "SELECT * FROM feature WHERE uuid = ? AND status = 1";
        try {
            BeanPropertyRowMapper<Feature> rowMapper = new BeanPropertyRowMapper<>(Feature.class);
            Feature feature = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting feature by uuid " + uuid);
            return Optional.of(feature);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Feature[]> getAll() {
        String sql = "SELECT * FROM feature WHERE status = 1";
        try {
            LinkedList<Feature> featureList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                Feature feature = new Feature();

                feature.setId(new Long((Integer) row.get("id")));
                feature.setUuid((String) row.get("uuid"));
                feature.setName((String) row.get("name"));
                feature.setCategory((Integer) row.get("category"));
                feature.setStatus((Integer) row.get("status"));
                feature.setCreatedAt((Date) row.get("created_at"));
                feature.setUpdatedAt((Date) row.get("updated_at"));

                featureList.add(feature);
            }

            logger.debug("Getting all features");
            return Optional.of(featureList.toArray(new Feature[featureList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Feature[]> search(String query) {
        String sql = "SELECT * FROM feature WHERE status = 1 AND name LIKE ?";
        try {
            LinkedList<Feature> featureList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, String.format("%%%s%%", query));
            for(Map<String, Object> row : rows) {
                Feature feature = new Feature();

                feature.setId(new Long((Integer) row.get("id")));
                feature.setUuid((String) row.get("uuid"));
                feature.setName((String) row.get("name"));
                feature.setCategory((Integer) row.get("category"));
                feature.setStatus((Integer) row.get("status"));
                feature.setCreatedAt((Date) row.get("created_at"));
                feature.setUpdatedAt((Date) row.get("updated_at"));

                featureList.add(feature);
            }

            logger.debug("Getting all features");
            return Optional.of(featureList.toArray(new Feature[featureList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Feature> insert(Feature feature) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO feature (uuid, name, category, status, created_at, updated_at) " +
                "VALUE (?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, feature.getName(),
                    feature.getCategory(), feature.getStatus(),
                    Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
            logger.debug("Creating feature with uuid: " + newUuid);
            return getByUuid(newUuid);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public boolean delete(String uuid) {
        String sql = "UPDATE feature SET " +
                "status=0 WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, uuid);
            logger.debug(String.format("Deleting feature: %s", uuid));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
