package com.me4399.mkt.dynamic_cron.service.impl;

import com.me4399.mkt.dynamic_cron.bean.A;
import com.me4399.mkt.dynamic_cron.dao.AMapper;
import com.me4399.mkt.dynamic_cron.service.AService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/12/17:30:10
 * @Description:
 */
@Service
public class AServiceImpl implements AService {

    @Resource
    private AMapper aMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return aMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(A record) {
        return aMapper.insert(record);
    }

    @Override
    public int insertSelective(A record) {
        return aMapper.insertSelective(record);
    }

    @Override
    public A selectByPrimaryKey(Integer id) {
        return aMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(A record) {
        return aMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(A record) {
        return aMapper.updateByPrimaryKey(record);
    }
}
