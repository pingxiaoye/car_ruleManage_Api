package com.example.car.controller;

import com.example.car.common.JsonResult;
import com.example.car.common.PageResult;
import com.example.car.common.ServiceException;
import com.example.car.entity.CarEntity;
import com.example.car.entity.RepairInfo;
import com.example.car.entity.UserEntity;
import com.example.car.mapper.CarMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/car")
public class CarController {

    @Resource
    private CarMapper carMapper;

    /**
     * 新增车辆,关联用户
     */
    @RequestMapping("/add")
    @Transactional
    public JsonResult add(@RequestBody CarEntity carEntity){
        try {
            carMapper.insert(carEntity);
            if (carEntity.getImgList()!=null){
                carMapper.insertImg(carEntity.getImgList(),carEntity.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("添加失败");
        }
        return new JsonResult();
    }

    /**
     * 获取车辆列表
     * @param carEntity
     * @return
     */
    @RequestMapping("/list")
    public JsonResult getList(@RequestBody CarEntity carEntity){
        int rowCount = carMapper.getListCount(carEntity);
        PageResult<CarEntity> pageResult = new PageResult<>(carEntity.getCurrent(), carEntity.getSize(), rowCount);
        List<CarEntity> list = carMapper.getList(pageResult,carEntity);
        for (CarEntity c:list){
            List<String> imgList = carMapper.getImgList(c.getId());
            c.setImgList(imgList);
        }
        pageResult.setPageData(list);
        return new JsonResult(pageResult);
    }

    @RequestMapping("/del")
    public JsonResult del(@RequestParam int carId){
        carMapper.deleteByPrimaryKey(carId);
        return new JsonResult();
    }

    /**
     * 添加维修记录
     * @param repairInfo
     * @return
     */
    @RequestMapping("/addRepairInfo")
    public JsonResult addRepairInfo(@RequestBody RepairInfo repairInfo){
        carMapper.insertRepairInfo(repairInfo);
        return new JsonResult();
    }


    /**
     * 获取维修记录
     * @param current
     * @param size
     * @param carId
     * @return
     */
    @RequestMapping("/getRepairInfoList")
    public JsonResult getRepairInfoList(Integer current,Integer size,Integer carId){
        int rowCount = carMapper.getRepairInfoListCount(carId);
        PageResult<RepairInfo> pageResult = new PageResult<>(current, size, rowCount);
        List<RepairInfo> list = carMapper.getRepairInfoList(pageResult,carId);
        pageResult.setPageData(list);
        return new JsonResult(pageResult);
    }


}
