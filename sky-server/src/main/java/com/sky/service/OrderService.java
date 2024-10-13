package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO findById(Long id);

    void repate(Long id);

    void cancel(Long id);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void confirm(Long id);

    void reject(OrdersRejectionDTO ordersRejectionDTO);

    void cancelAdmin(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);
    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    OrderStatisticsVO statistic();

    void remind(Long id);
}
