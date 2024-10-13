package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     *
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时时间:{}", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> byStatusAndOrderTimeLt = orderMapper.getByStatusAndOrderTimeLt(Orders.PENDING_PAYMENT, localDateTime);
        if(byStatusAndOrderTimeLt!=null&&byStatusAndOrderTimeLt.size()>0){
            for (Orders orders : byStatusAndOrderTimeLt) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时,自己取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }


    }


    /**
    处理一直是派送中的订单

     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理派送中的订单:{}",LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLt(Orders.DELIVERY_IN_PROGRESS, localDateTime);

        for (Orders orders : ordersList) {
            orders.setStatus(Orders.COMPLETED);
            orderMapper.update(orders);
        }
    }
}
