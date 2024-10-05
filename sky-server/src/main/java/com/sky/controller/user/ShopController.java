package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/status")
    @ApiOperation("查询铺营业状态")
    public Result<Integer> find(){
        Integer o = (Integer) redisTemplate.opsForValue().get("shopStatus");
        log.info("获取店铺营业状态{}",o==1?"营业中":"停业中");
        return Result.success(o);
    }
}
