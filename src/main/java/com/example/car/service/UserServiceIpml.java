package com.example.car.service;

import com.example.car.common.PageResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.UserEntity;
import com.example.car.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceIpml {
    @Resource
    private UserMapper userMapper;
    public UserEntity getUserByUserName(String userName) {
        UserEntity userEntity = userMapper.getUserByUserName(userName);
        return userEntity;
    }

    public void addUser(UserEntity userEntity) {
        try {
            userMapper.insert(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("添加用户失败");
        }
    }


    public PageResult<UserEntity> getUserList(UserEntity userEntity) {
        int rowCount = userMapper.getUserListCount();
        PageResult<UserEntity> pageResult = new PageResult<>(userEntity.getCurrent(), userEntity.getSize(), rowCount);
        if (pageResult.willCauseEmptyList()) {
            return pageResult;
        }
        List<UserEntity> List = userMapper.getUserList(pageResult,userEntity);
        pageResult.setPageData(List);
        return pageResult;
    }
}
