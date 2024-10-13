package com.sky.controller.user;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "C端订单接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);

    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查看订单详清")
    public Result<OrderVO> findOrderDetail(@PathVariable Long id){
        log.info("订单详情{}",id);
        OrderVO orderV0 = orderService.findById(id);
        return Result.success(orderV0);

    }
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repete(@PathVariable Long id){
        orderService.repate(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cacel(@PathVariable Long id){
        orderService.cancel(id);
        return Result.success();
    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("历史订单分页查询{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("客户催单")
    public Result remind(@PathVariable("id") Long id){
        orderService.remind(id);
     return Result.success();
    }

}
