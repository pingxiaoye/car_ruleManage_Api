package com.example.car.controller;

import com.example.car.common.JsonResult;
import com.example.car.common.PageResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.UserEntity;
import com.example.car.mapper.UserMapper;
import com.example.car.service.UserServiceIpml;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceIpml userServiceIpml;
    @Resource
    private UserMapper userMapper;


    @RequestMapping("/add")
    public JsonResult addUser(@RequestBody UserEntity userEntity){
        UserEntity user = userServiceIpml.getUserByUserName(userEntity.getUserName());
        if (user!=null){
            throw new ServiceException("当前用户名已存在!");
        }
        //默认积分12
        if(userEntity.getPoints()==null){
            userEntity.setPoints(12);
        }
        userServiceIpml.addUser(userEntity);
        return new JsonResult();
    }

    @RequestMapping("/del")
    public JsonResult delUser(@RequestParam Integer userId){
        UserEntity userEntity = userMapper.selectByPrimaryKey(userId);
        if (userEntity==null){
            throw new ServiceException("当前用户不存在!");
        }
        userMapper.deleteByPrimaryKey(userId);
        return new JsonResult();
    }

    @RequestMapping("/update")
    public JsonResult updateUser(@RequestBody UserEntity userEntity){
        UserEntity user = userMapper.selectByPrimaryKey(userEntity.getId());
        if (user==null){
            throw new ServiceException("当前用户不存在!");
        }
        userMapper.updateByPrimaryKeySelective(userEntity);
        return new JsonResult();
    }

    @RequestMapping("/list")
    public JsonResult getUserList(@RequestBody UserEntity userEntity){
        PageResult<UserEntity> pageResult = userServiceIpml.getUserList(userEntity);
        return new JsonResult(pageResult);
    }

}
