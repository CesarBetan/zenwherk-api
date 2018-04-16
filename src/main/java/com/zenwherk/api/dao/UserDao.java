package com.zenwherk.api.dao;

import com.zenwherk.api.domain.User;
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
public class UserDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ? AND status = 1";
        try {
            BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
            User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
            logger.debug("Getting user by id " + id);
            return Optional.of(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<User> getByUuid(String uuid) {
        String sql = "SELECT * FROM user WHERE uuid = ? AND status = 1";
        try {
            BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
            User user = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting user by uuid " + uuid);
            return Optional.of(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> getByEmail(String email){
        String sql = "SELECT * FROM user WHERE email = ? AND status = 1";
        try {
            BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
            User user = jdbcTemplate.queryForObject(sql, rowMapper, email);
            logger.debug("Getting user by email " +  email);
            return Optional.of(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User[]> getAll(){
        String sql = "SELECT * FROM user WHERE status = 1";
        try {
            LinkedList<User> userList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                User user = new User();

                user.setId(new Long((Integer) row.get("id")));
                user.setUuid((String) row.get("uuid"));
                user.setName((String) row.get("name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setPasswordHash((String) row.get("password_hash"));
                user.setPicture((String) row.get("picture"));
                user.setRole((Integer) row.get("role"));
                user.setStatus((Integer) row.get("status"));
                user.setCreatedAt((Date) row.get("created_at"));
                user.setUpdatedAt((Date) row.get("updated_at"));

                userList.add(user);
            }

            logger.debug("Getting all users");
            return Optional.of(userList.toArray(new User[userList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<User[]> search(String query) {
        String sql = "SELECT * FROM user WHERE status = 1 AND email LIKE ?";
        try {
            LinkedList<User> userList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, String.format("%%%s%%", query));
            for(Map<String, Object> row : rows) {
                User user = new User();

                user.setId(new Long((Integer) row.get("id")));
                user.setUuid((String) row.get("uuid"));
                user.setName((String) row.get("name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setPasswordHash((String) row.get("password_hash"));
                user.setPicture((String) row.get("picture"));
                user.setRole((Integer) row.get("role"));
                user.setStatus((Integer) row.get("status"));
                user.setCreatedAt((Date) row.get("created_at"));
                user.setUpdatedAt((Date) row.get("updated_at"));

                userList.add(user);
            }

            logger.debug("Searching for users");
            return Optional.of(userList.toArray(new User[userList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<User> insert(User user) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO user (uuid, name, last_name, email, password_hash, " +
                "picture, role, status, created_at, updated_at) " +
                "VALUE (?,?,?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, user.getName(), user.getLastName(),
                    user.getEmail(), user.getPasswordHash(), user.getPicture(),
                    user.getRole(), user.getStatus(), Timestamp.from(Instant.now()),
                    Timestamp.from(Instant.now()));
            logger.debug(String.format("Creating user: %s %s", user.getName(), user.getLastName()) );
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> update(User user) {
        String sql = "UPDATE user SET " +
                "name=?, last_name=?, picture=?, password_hash=?, updated_at=? WHERE uuid=?";
        try {
            jdbcTemplate.update(sql, user.getName(), user.getLastName(), user.getPicture(),
                    user.getPasswordHash(), Timestamp.from(Instant.now()), user.getUuid());
            logger.debug(String.format("Updating user: %s", user.getUuid()));
            return getByUuid(user.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
