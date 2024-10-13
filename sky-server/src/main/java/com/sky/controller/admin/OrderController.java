package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/admin/order")
@Api(tags = "订单管理接口")
@RestController("adminOrderController")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        Long id = ordersConfirmDTO.getId();
        log.info("接单{}",id);
       orderService.confirm(id);
       return Result.success();
    }
    @GetMapping("/conditionSearch")
    @ApiOperation("订单管理接口")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> findDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.findById(id);
        return Result.success(orderVO);
    }
    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 取消订单
     *
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.cancelAdmin(ordersCancelDTO);
        return Result.success();
    }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        orderService.delivery(id);
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }
    @GetMapping("/statistics")
    @ApiOperation("各个订单状态数量统计")
    public Result<OrderStatisticsVO> orderStatisticsVOResult(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistic();
        return Result.success(orderStatisticsVO);
    }
}
