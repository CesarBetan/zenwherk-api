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
import java.util.*;

@Repository
public class PlaceDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceDao.class);

    public Optional<Place> getById(Long id) {
        String sql = "SELECT * FROM place WHERE id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<Place> rowMapper = new BeanPropertyRowMapper<>(Place.class);
            Place place = jdbcTemplate.queryForObject(sql, rowMapper, id);
            logger.info("Getting place by id " + id);
            return Optional.of(place);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }


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

    public Optional<Place> update(Place place) {
        String sql = "UPDATE place SET " +
                "name=?, address=?, description=?, phone=?, category=?, " +
                "website=?, latitude=?, longitude=?, status=?, " +
                "updated_at=? WHERE uuid=?";

        try {
            jdbcTemplate.update(sql, place.getName(), place.getAddress(),
                    place.getDescription(), place.getPhone(), place.getCategory(),
                    place.getWebsite(), place.getLatitude(), place.getLongitude(),
                    place.getStatus(), Timestamp.from(Instant.now()),
                    place.getUuid());
            logger.debug(String.format("Updating place: %s", place.getUuid()));
            return getByUuid(place.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    private Place[] toPlaceArray(List<Map<String, Object>> rows, boolean addDistance) {
        LinkedList<Place> placeList = new LinkedList<>();
        for(Map<String, Object> row : rows) {
            Place place = new Place();

            place.setId(new Long((Integer) row.get("id")));
            place.setUuid((String) row.get("uuid"));
            place.setName((String) row.get("name"));
            place.setAddress((String) row.get("address"));
            place.setDescription((String) row.get("description"));
            place.setPhone((String) row.get("phone"));
            place.setCategory((Integer) row.get("category"));
            place.setWebsite((String) row.get("website"));
            place.setLatitude(new Double((Float) row.get("latitude")));
            place.setLongitude(new Double((Float) row.get("longitude")));
            place.setStatus((Integer) row.get("status"));
            place.setCreatedAt((Date) row.get("created_at"));
            place.setUpdatedAt((Date) row.get("updated_at"));
            place.setUploadedBy(new Long((Integer) row.get("uploaded_by")));

            if(addDistance) {
                place.setDistanceInKm((Double) row.get("distance_in_km"));
            }

            placeList.add(place);
        }
        return placeList.toArray(new Place[placeList.size()]);
    }

    public Optional<Place[]> searchApprovedPlaces(String query, List<String> categories) {
        String sql = "SELECT * FROM place WHERE status IN(1,3) ";
        if(query != null && query.trim().length() > 0) {
            // The user wants to search by name, filter the query
            sql += "AND name LIKE ? ";
        }
        if(categories != null && categories.size() > 0) {
            // The user wants to filter by category
            sql += "AND category IN (";
            for(int i = 0; i < categories.size(); i++) {
                if(i == 0) {
                    sql += categories.get(i);
                } else {
                    sql += String.format(", %s", categories.get(i));
                }
            }
            sql += ") ";
        }
        try {
            List<Map<String, Object>> rows;
            if(query != null && query.trim().length() > 0) {
                rows = jdbcTemplate.queryForList(sql, String.format("%%%s%%", query));
            } else {
                rows = jdbcTemplate.queryForList(sql);
            }
            Place[] places = toPlaceArray(rows, false);
            logger.debug("Getting all approved places");
            return Optional.of(places);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Place[]> searchNearPlaces(Double latitude, Double longitude) {
        String sql = "SELECT * FROM (SELECT id, uuid, name, address, description, phone, category, " +
                "website, latitude, longitude, status, created_at, updated_at, uploaded_by, " +
                "111.045 * DEGREES(ACOS(COS(RADIANS(?)) * COS(RADIANS(latitude)) " +
                "* COS(RADIANS(longitude) - RADIANS(?)) + SIN(RADIANS(?)) * SIN(RADIANS(latitude)))) " +
                "AS distance_in_km " +
                "FROM place WHERE status IN(1,3) " +
                "ORDER BY distance_in_km ASC " +
                "LIMIT 0,20) AS r WHERE distance_in_km < 10.0";
        try {
            // latitude, longitude, latitude
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, latitude, longitude, latitude);
            Place[] places = toPlaceArray(rows, true);
            logger.debug("Getting all approved near places");
            return Optional.of(places);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Place[]> getPlacesToBeAdded() {
        String sql = "SELECT * FROM place WHERE status IN (2,3)";
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            Place[] places = toPlaceArray(rows, false);
            logger.debug("Getting all places to be added");
            return Optional.of(places);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Place[]> searchFeaturedPlaces() {
        String sql =
            "SELECT " +
            "approved_places.id, approved_places.uuid, approved_places.name, " +
                    "approved_places.address, approved_places.description, " +
                    "approved_places.phone, approved_places.category, " +
                    "approved_places.website, approved_places.latitude, " +
                    "approved_places.longitude, approved_places.status, " +
                    "approved_places.created_at, approved_places.updated_at, " +
                    "approved_places.uploaded_by, COALESCE(ratings.rating, 0.0) AS rating " +
            "FROM " +
                    "(SELECT * FROM place WHERE status IN (1,3)) AS approved_places " +
            "LEFT JOIN " +
            "(SELECT place_id, AVG(review_rating) AS rating FROM review WHERE status=1 GROUP BY place_id) AS ratings " +
            "ON approved_places.id=ratings.place_id " +
            "ORDER BY COALESCE(ratings.rating, 0.0) DESC LIMIT 2 ";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            Place[] places = toPlaceArray(rows, false);
            logger.info("Getting featured places");
            return Optional.of(places);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
