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
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public Optional<User> getByUuid(String uuid) {
        String sql = "SELECT * FROM user WHERE uuid = ?";
        try {
            BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
            User user = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Obteniendo usuario por uuid " + uuid);
            return Optional.of(user);
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
            jdbcTemplate.update(sql, newUuid, user.getName(), user.getLast_name(),
                    user.getEmail(), user.getPassword_hash(), user.getPicture(),
                    user.getRole(), user.getStatus(), Timestamp.from(Instant.now()),
                    Timestamp.from(Instant.now()));
            logger.debug(String.format("Insertando usuario: %s %s", user.getName(), user.getLast_name()) );
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
