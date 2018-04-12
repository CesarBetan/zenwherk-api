package com.zenwherk.api.dao;

import com.zenwherk.api.domain.PasswordRecoveryToken;
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
public class PasswordRecoveryTokenDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryTokenDao.class);

    public boolean deletePasswordRecoveryTokenByUserUserId(Long userId) {
        String sql = "DELETE FROM password_recovery_token WHERE user_id=?";
        try {
            jdbcTemplate.update(sql, userId);
            logger.debug("Deleting password recovery tokens for user: " + userId);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return false;
    }

    public Optional<PasswordRecoveryToken> getByUuid(String uuid) {
        String sql = "SELECT * FROM password_recovery_token WHERE uuid = ?";
        try {
            BeanPropertyRowMapper<PasswordRecoveryToken> rowMapper = new BeanPropertyRowMapper<>(PasswordRecoveryToken.class);
            PasswordRecoveryToken passwordRecoveryToken = jdbcTemplate.queryForObject(sql, rowMapper, uuid);
            logger.debug("Getting passwordRecoveryToken by uuid " + uuid);
            return Optional.of(passwordRecoveryToken);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<PasswordRecoveryToken> insert(PasswordRecoveryToken passwordRecoveryToken) {
        String newUuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO password_recovery_token (uuid, token, expiration_date, " +
                "status, created_at, updated_at, user_id) " +
                "VALUE (?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql, newUuid, passwordRecoveryToken.getToken(),
                    passwordRecoveryToken.getExpirationDate(), passwordRecoveryToken.getStatus(),
                    Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    passwordRecoveryToken.getUserId());
            logger.debug("Creating password recovery token with uuid: " + newUuid);
            return getByUuid(newUuid);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
