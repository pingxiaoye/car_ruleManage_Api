package com.example.car.controller;

import com.example.car.common.JsonResult;
import com.example.car.common.PageResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.BrokeInfoEntity;
import com.example.car.entity.UserEntity;
import com.example.car.mapper.BrokeInfoMapper;
import com.example.car.mapper.UserMapper;
import com.example.car.service.BrokeInfoServiceImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/brokeInfo")
public class BrokeInfoController {

    @Resource
    private BrokeInfoServiceImpl brokeInfoService;
    @Resource
    private BrokeInfoMapper brokeInfoMapper;
    @Resource
    private UserMapper userMapper;

    @RequestMapping("/add")
    public JsonResult add(@RequestBody BrokeInfoEntity brokeInfoEntity){
        try {
            if (brokeInfoEntity.getUserId()!=null){
                //减积分
                Integer userId = brokeInfoEntity.getUserId();
                UserEntity userEntity = userMapper.selectByPrimaryKey(userId);
                userEntity.setPoints(userEntity.getPoints()-brokeInfoEntity.getPoints());
                userMapper.updateByPrimaryKeySelective(userEntity);
            }
            //插入违章记录
            brokeInfoMapper.insert(brokeInfoEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("添加失败");
        }
        return new JsonResult();
    }

    @RequestMapping("/list")
    public JsonResult list(@RequestBody BrokeInfoEntity brokeInfoEntity){
        PageResult<BrokeInfoEntity> pageResult = brokeInfoService.getList(brokeInfoEntity);
        return new JsonResult(pageResult);
    }

    @RequestMapping("/update")
    public JsonResult update(@RequestBody BrokeInfoEntity brokeInfoEntity){
        try {
            brokeInfoMapper.updateByPrimaryKeySelective(brokeInfoEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("更新失败");
        }
        return new JsonResult();
    }

    @RequestMapping("/del")
    public JsonResult del(@RequestParam Integer brokeInfoId){
        try {
            brokeInfoMapper.deleteByPrimaryKey(brokeInfoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("删除失败");
        }
        return new JsonResult();
    }

    @RequestMapping("/handle")
    public JsonResult handle(@NotNull Integer userId, @NotNull Integer brokeInfoId){
        BrokeInfoEntity brokeInfoEntity = brokeInfoMapper.selectByPrimaryKey(brokeInfoId);
        if(brokeInfoEntity.getUserId()!=null){
            throw new ServiceException("当前违章已处理!不可重复处理!");
        }
        try {
            UserEntity userEntity = userMapper.selectByPrimaryKey(userId);

            userEntity.setPoints(userEntity.getPoints()-brokeInfoEntity.getPoints());
            userMapper.updateByPrimaryKeySelective(userEntity);

            brokeInfoEntity.setUserId(userId);
            brokeInfoMapper.updateByPrimaryKeySelective(brokeInfoEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("处理失败");
        }
        return new JsonResult();
    }

}
