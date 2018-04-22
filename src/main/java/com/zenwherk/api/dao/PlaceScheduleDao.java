package com.zenwherk.api.dao;

import com.zenwherk.api.domain.PlaceSchedule;
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
public class PlaceScheduleDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlaceScheduleDao.class);

    public Optional<PlaceSchedule> getById(Long id) {
        String sql = "SELECT * FROM place_schedule WHERE id = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceSchedule> rowMapper = new BeanPropertyRowMapper<>(PlaceSchedule.class);
            PlaceSchedule placeSchedule = jdbcTemplate.queryForObject(sql, rowMapper, id);
            logger.debug("Getting place schedule by id " + id);
            return Optional.of(placeSchedule);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceSchedule> getByUuid(String uuid) {
        String sql = "SELECT * FROM place_schedule WHERE uuid = ? AND status > 0";
        try {
            BeanPropertyRowMapper<PlaceSchedule> rowMapper = new BeanPropertyRowMapper<>(PlaceSchedule.class);
            PlaceSchedule placeSchedule = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting place schedule by uuid " + uuid);
            return Optional.of(placeSchedule);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceSchedule[]> getApprovedPlaceSchedulesByDayAndPlaceId(Integer day, Long placeId){
        String sql = "SELECT * FROM place_schedule WHERE status IN(1,3) AND day=? AND place_id=?";
        try {
            LinkedList<PlaceSchedule> placeSchedulesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, day, placeId);

            for(Map<String, Object> row : rows) {
                PlaceSchedule placeSchedule = new PlaceSchedule();

                placeSchedule.setId(new Long((Integer) row.get("id")));
                placeSchedule.setUuid((String) row.get("uuid"));
                placeSchedule.setDay((Integer) row.get("day"));
                placeSchedule.setOpenTime((Date) row.get("open_time"));
                placeSchedule.setCloseTime((Date) row.get("close_time"));
                placeSchedule.setStatus((Integer) row.get("status"));
                placeSchedule.setCreatedAt((Date) row.get("created_at"));
                placeSchedule.setUpdatedAt((Date) row.get("updated_at"));
                placeSchedule.setPlaceId(new Long((Integer) row.get("place_id")));
                placeSchedule.setUploadedBy(new Long((Integer) row.get("uploaded_by")));

                placeSchedulesList.add(placeSchedule);
            }
            logger.debug("Obtaining approved place schedules by day and id");
            return Optional.of(placeSchedulesList.toArray(new PlaceSchedule[placeSchedulesList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<PlaceSchedule[]> getApprovedPlaceSchedulesByPlaceId(Long placeId) {
        String sql = "SELECT * FROM place_schedule WHERE status IN (1,3) AND place_id=?";
        try {
            LinkedList<PlaceSchedule> placeSchedulesList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, placeId);

            for(Map<String, Object> row : rows) {
                PlaceSchedule placeSchedule = new PlaceSchedule();

                placeSchedule.setId(new Long((Integer) row.get("id")));
                placeSchedule.setUuid((String) row.get("uuid"));
                placeSchedule.setDay((Integer) row.get("day"));
                placeSchedule.setOpenTime((Date) row.get("open_time"));
                placeSchedule.setCloseTime((Date) row.get("close_time"));
                placeSchedule.setStatus((Integer) row.get("status"));
                placeSchedule.setCreatedAt((Date) row.get("created_at"));
                placeSchedule.setUpdatedAt((Date) row.get("updated_at"));
                placeSchedule.setPlaceId(new Long((Integer) row.get("place_id")));
                placeSchedule.setUploadedBy(new Long((Integer) row.get("uploaded_by")));

                placeSchedulesList.add(placeSchedule);
            }
            logger.debug("Obtaining approved place schedules");
            return Optional.of(placeSchedulesList.toArray(new PlaceSchedule[placeSchedulesList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceSchedule> insert(PlaceSchedule placeSchedule) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO place_schedule (uuid, day, open_time, close_time, status, created_at, " +
                "updated_at, place_id, uploaded_by) " +
                "VALUE (?,?,?,?,?,?,?,?,?)";

        try {
            jdbcTemplate.update(sql, newUuid, placeSchedule.getDay(), placeSchedule.getOpenTime(),
                    placeSchedule.getCloseTime(), placeSchedule.getStatus(),
                    Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    placeSchedule.getPlaceId(), placeSchedule.getUploadedBy());
            logger.debug(String.format("Creating place schedule: %s", newUuid));
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PlaceSchedule> update(PlaceSchedule placeSchedule) {
        String sql = "UPDATE place_schedule SET " +
                "day=?, open_time=?, close_time=?, status=?, " +
                "updated_at=?, uploaded_by=? " +
                "WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, placeSchedule.getDay(), placeSchedule.getOpenTime(),
                    placeSchedule.getCloseTime(), placeSchedule.getStatus(),
                    Timestamp.from(Instant.now()), placeSchedule.getUploadedBy(),
                    placeSchedule.getUuid());
            logger.debug(String.format("Updating place schedule: %s", placeSchedule.getUuid()));
            return getByUuid(placeSchedule.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public boolean deleteByDayAndPlaceId(Integer day, Long placeId) {
        String sql = "UPDATE place_schedule SET " +
                "status=0, updated_at=? WHERE day=? AND place_id=?";
        try {
            jdbcTemplate.update(sql,Timestamp.from(Instant.now()), day, placeId);
            logger.debug(String.format("Updating place schedule with place id: %d", placeId));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }
}
