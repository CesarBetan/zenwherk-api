package com.zenwherk.api.service;


import com.zenwherk.api.dao.StatsDao;
import com.zenwherk.api.domain.Stats;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatsService {

    @Autowired
    private StatsDao statsDao;

    public ListResult<Stats> getNewUsersLastWeek() {
        ListResult<Stats> result = new ListResult();

        Optional<Stats[]> newUsers = statsDao.getNewUsers();
        if(!newUsers.isPresent()) {
            result.setErrorCode(500);
            result.setMessage(new Message("Error de servidor"));
            return result;
        }

        result.setData(newUsers);
        return result;
    }
}
