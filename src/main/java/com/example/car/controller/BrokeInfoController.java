package com.example.car.controller;

import com.example.car.common.JsonResult;
import com.example.car.common.PageResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.BrokeInfoEntity;
import com.example.car.entity.CarEntity;
import com.example.car.entity.UserEntity;
import com.example.car.mapper.BrokeInfoMapper;
import com.example.car.mapper.CarMapper;
import com.example.car.mapper.UserMapper;
import com.example.car.service.BrokeInfoServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/api/brokeInfo")
public class BrokeInfoController {

    @Resource
    private BrokeInfoServiceImpl brokeInfoService;
    @Resource
    private BrokeInfoMapper brokeInfoMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CarMapper carMapper;

    @RequestMapping("/add")
    @Transactional
    public JsonResult add(@RequestBody BrokeInfoEntity brokeInfoEntity){
            CarEntity carEntity = carMapper.getByCardNumber(brokeInfoEntity.getCardNumber());
            if (carEntity==null){
                throw new ServiceException("当前车牌号不存在！");
            }
            brokeInfoEntity.setUserId(carEntity.getUserId());
            brokeInfoEntity.setHandle("未处理");
            //插入违章记录
            brokeInfoMapper.insert(brokeInfoEntity);
            Integer bId = brokeInfoEntity.getId();
            if (brokeInfoEntity.getImgList()!=null){
                brokeInfoMapper.insertImg(brokeInfoEntity.getImgList(),bId);
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
            if(brokeInfoEntity.getImgList()!=null){
                //先删除之前的图片
                brokeInfoMapper.delImgs(brokeInfoEntity.getId());
                //再重新插入图片
                brokeInfoMapper.insertImg(brokeInfoEntity.getImgList(),brokeInfoEntity.getId());
            }
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
        if(brokeInfoEntity.getHandle().equals("已处理")){
            throw new ServiceException("当前违章已处理!不可重复处理!");
        }
        try {
            UserEntity userEntity = userMapper.selectByPrimaryKey(userId);

            userEntity.setPoints(userEntity.getPoints()-brokeInfoEntity.getPoints());
            userMapper.updateByPrimaryKeySelective(userEntity);
            brokeInfoEntity.setHandle("已处理");
            brokeInfoEntity.setUserId(userId);
            brokeInfoMapper.updateByPrimaryKeySelective(brokeInfoEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("处理失败");
        }
        return new JsonResult();
    }

}
