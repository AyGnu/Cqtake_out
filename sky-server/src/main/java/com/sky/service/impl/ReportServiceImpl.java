package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO findByData(LocalDate begin, LocalDate end) {
        //当前集合用于存放beig但end范围内每天的日期
        List<LocalDate> dateList  =new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //计算指定日期的后一天对应的日期
            begin= begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnOverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnOver = orderMapper.sumByMap(map);
            //查询date日期对应的营业额,营业额是指状态为“已完成的金额合计”
            //select sum(amount)
            turnOver = turnOver==null?0.0:turnOver;
            turnOverList.add(turnOver);

        }
        //        dateList.add(begin.plusDays(1));
    return TurnoverReportVO.builder().
            dateList( StringUtils.join(dateList,","))
            .turnoverList(StringUtils.join(turnOverList,","))
            .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList  =new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //计算指定日期的后一天对应的日期
            begin= begin.plusDays(1);
            dateList.add(begin);
        }
        //每天新增
        List<Integer> newUserList = new ArrayList<>();
        //每天总
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end",endTime);
             Integer totalUser  =  userMapper.countByMap(map);
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);

        }
        // select count(*) from user where createTIme< && crateTime >
        return UserReportVO.builder().dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 根据时间区间统计订单数量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end){
        //1.准备日期条件：和营业额功能相同，不在赘述。
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //同样需要把集合类型转化为字符串类型并用逗号分隔
        String data = StringUtils.join(dateList, ",");

        //2.准备每一天对应的订单数量：订单总数  有效订单数
        //每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数集合
        List<Integer> validOrderCountList = new ArrayList<>();

        /**
         * 思路分析：查询的是订单表
         * 查询每天的总订单数：只需要根据下单时间计算出当天的起始时间和结束时间作为查询条件，
         *                 就是当天的总订单数。
         * 查询每天的有效订单数：根据下单时间计算出当天的起始时间和结束时间以及状态已完成（代表有效订单）的订单作为查询条件，
         *                   就是每天的有效订单数
         * 同样没必要写2个sql，因为这2个SQL的主体结构相同，只是查询的条件不同，所以没有必要写2个sql只需要写一个动态的sql即可。
         */
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//起始时间 包含年月日时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//结束时间
            //查询每天的总订单数 select count(id) from orders where order_time > ? and order_time < ?
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = ?
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //同样需要把集合类型转化为字符串类型并用逗号分隔
        String orderCount1 = StringUtils.join(orderCountList, ",");//每天订单总数集合
        String validOrderCount1 = StringUtils.join(validOrderCountList, ",");//每天有效订单数集合

        /**
         * 3. 准备时间区间内的订单数：时间区间内的总订单数   时间区间内的总有效订单数
         * 思路分析：
         *    订单总数：整个这个时间区域内订单总的数量，根据这个时间去卡查询数据库可以查询出来。
         *    时间区间内的总有效订单数：也可以通过查询数据库查出来。
         *    实际上不查询数据库，总订单数和总有效订单数也能计算出来：
         *         因为上面2个集合中已经查询保存了，这个时间段之内每天的总订单数和总有效订单数，
         *         所以只需要分别遍历这2个集合，每天的订单总数加一起就是整个时间段总订单数，
         *         每天的有效订单数加起来就是整个时间段的总有效订单数。
         */
        //计算时间区域内的总订单数量
        //Integer totalOrderCounts = orderCountList.stream().reduce(Integer::sum).get();//方式一：简写方式
        Integer totalOrderCounts = 0;
        for (Integer integer : orderCountList) {  //方式二：普通for循环方式
            totalOrderCounts = totalOrderCounts+integer;
        }
        //计算时间区域内的总有效订单数量
        //Integer validOrderCounts = validOrderCountList.stream().reduce(Integer::sum).get();//方式一：简写方式
        Integer validOrderCounts = 0;
        for (Integer integer : validOrderCountList) { //方式二：普通for循环方式
            validOrderCounts = validOrderCounts+integer;
        }

        //4.订单完成率：  总有效订单数量/总订单数量=订单完成率
        Double orderCompletionRate = 0.0;  //订单完成率的初始值
        if(totalOrderCounts != 0){ //防止分母为0出现异常
            //总有效订单数量和总有效订单数量都是Integer类型，这里使用的是Double类型接收所以需要进行转化
            orderCompletionRate = validOrderCounts.doubleValue() / totalOrderCounts;
        }

        //构造vo对象
        return OrderReportVO.builder()
                .dateList(data)  //x轴日期数据
                .orderCountList(orderCount1) //y轴每天订单总数
                .validOrderCountList(validOrderCount1)//y轴每天有效订单总数
                .totalOrderCount(totalOrderCounts) //时间区域内总订单数
                .validOrderCount(validOrderCounts) //时间区域内总有效订单数
                .orderCompletionRate(orderCompletionRate) //订单完成率
                .build();

    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //mapper传递的参数是LocalDateTime类型，impl层传递的参数是LocalDate所以需要进行转化
        //   当前日期的起始时间      结束日期最后的时间点
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        /**
         * 获取的是GoodsSalesDTO类型的集合数据：String name商品名称     Integer number销量
         * 需要获得是vo对象类型： String nameList 商品名称列表   以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
         *                    String numberList销量列表     以逗号分隔，例如：260,215,200
         * 所以需要进行转化：取出GoodsSalesDTO集合中所有的name属性取出来拼接到一起，并且以逗号分隔
         *                                             （恰好对应vo中的nameList）
         *               取出GoodsSalesDTO集合中所有的number属性取出来拼接到一起，并且以逗号分隔
         *                                             （恰好对应vo中的numberList）
         *   方式一：通过 stream流的方式简写
         *   String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
         *   String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");
         */
        List<String> nameList1 = new ArrayList<>(); //商品名称
        List<Integer> numberList1 = new ArrayList<>(); //销量
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {//方式二：普通for循环
            nameList1.add(goodsSalesDTO.getName());
            numberList1.add(goodsSalesDTO.getNumber());
        }

        //获取的是list集合类型，需要转化为字符串并以逗号分隔
        //把list集合的每个元素取出来并且以逗号分隔，最终拼成一个字符串
        String nameList = StringUtils.join(nameList1, ",");
        String numberList = StringUtils.join(numberList1, ",");


        //封装vo对象并返回
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 根据时间区间统计指定状态的订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        //封装sql查询的条件为map集合，因为设计的mapper层传递的参数是使用map来封装的
        Map map = new HashMap();
        map.put("status", status);
        map.put("begin",beginTime);
        map.put("end", endTime);
        return orderMapper.countByMap(map);
    }


}
