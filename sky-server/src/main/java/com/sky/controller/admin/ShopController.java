package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态{}",status==1?"营业中":"停业中");
        redisTemplate.opsForValue().set("shopStatus",status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("查询铺营业状态")
    public Result<Integer> find(){
        Integer o = (Integer) redisTemplate.opsForValue().get("shopStatus");
        log.info("获取店铺营业状态{}",o==1?"营业中":"停业中");
        return Result.success(o);
    }
}
