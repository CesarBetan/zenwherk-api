package com.zenwherk.api.dao;

import com.zenwherk.api.domain.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StatsDao  {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StatsDao.class);

    public Optional<Stats[]> getNewUsers() {
        String sql =
                "SELECT d.date AS date, COUNT(u.id) AS users FROM " +
                "(SELECT CURDATE() AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -1 DAY) AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -2 DAY) AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -3 DAY) AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -4 DAY) AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -5 DAY) AS date " +
                "UNION ALL SELECT DATE_ADD(CURDATE(), INTERVAL -6 DAY) AS date) AS d " +
                "LEFT JOIN " +
                "(SELECT id, CAST(created_at AS DATE) AS created_date FROM user) AS u " +
                "ON d.date=u.created_date " +
                "GROUP BY d.date " +
                "ORDER BY d.date ASC";

        try {
            LinkedList<Stats> statsList = new LinkedList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for(Map<String, Object> row : rows) {
                Stats stat = new Stats();

                stat.setDate((Date) row.get("date"));
                stat.setUsers((Long) row.get("users"));

                statsList.add(stat);
            }

            logger.info("Getting new users last week");
            return Optional.of(statsList.toArray(new Stats[statsList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }
}
