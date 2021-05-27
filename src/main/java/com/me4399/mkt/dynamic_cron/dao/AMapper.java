package com.me4399.mkt.dynamic_cron.dao;

import com.me4399.mkt.dynamic_cron.bean.A;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(A record);

    int insertSelective(A record);

    A selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(A record);

    int updateByPrimaryKey(A record);
}