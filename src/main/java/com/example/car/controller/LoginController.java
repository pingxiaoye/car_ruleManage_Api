package com.example.car.controller;

import com.example.car.common.JsonResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.LoginUser;
import com.example.car.entity.UserEntity;
import com.example.car.service.UserServiceIpml;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/home")
public class LoginController {
    @Resource
    private UserServiceIpml userServiceIpml;

    @RequestMapping("/login")
    public JsonResult login(@RequestBody LoginUser loginUser){
        UserEntity user = userServiceIpml.getUserByUserName(loginUser.getUserName());
        if (user==null){
            throw new ServiceException("当前用户不存在!");
        }
        if(!user.getPassword().equals(loginUser.getPassword())){
            throw new ServiceException("密码错误!");
        }
        List<String> list = new ArrayList<>();
        if(user.getId()==1||user.getUserName().equals("admin")){
            list.add("admin");

        }else {
            list.add("normal");
        }
        user.setAuthority(list);
        return new JsonResult(user);
    }

}
