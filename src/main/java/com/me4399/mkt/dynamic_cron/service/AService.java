package com.me4399.mkt.dynamic_cron.service;

import com.me4399.mkt.dynamic_cron.bean.A;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/12/17:29:36
 * @Description:
 */
public interface AService {

    int deleteByPrimaryKey(Integer id);

    int insert(A record);

    int insertSelective(A record);

    A selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(A record);

    int updateByPrimaryKey(A record);
}
