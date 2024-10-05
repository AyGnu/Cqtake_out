package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/setmeal")
@RestController
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @PostMapping
    @ApiOperation("新增套餐")
    public Result add(@RequestBody SetmealDTO setmealDTO){
        setmealService.add(setmealDTO);
        return Result.success();
    }
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult>  page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询{}",setmealPageQueryDTO);
       PageResult pageResult = setmealService.page(setmealPageQueryDTO);

        return Result.success(pageResult);
    }
    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> findById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.findById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("套餐起售停售")
    @PostMapping("/status/{status}")
    public Result startOrStopSetmeal(@PathVariable Integer status,Long id){
        setmealService.startOrStopSetmeal(status,id);
        return Result.success();
    }

    @ApiOperation("批量删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }
}
