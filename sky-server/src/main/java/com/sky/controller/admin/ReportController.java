package com.sky.controller.admin;

import com.sky.mapper.ReportMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Api(tags = "数据统计相关接口")
@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    @ApiOperation("营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnOver(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end){
        log.info("查询营业额{}",begin,end);
        TurnoverReportVO turnoverReportVO = reportService.findByData(begin,end);
        return Result.success(turnoverReportVO);
    }
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics( @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                LocalDate end){
log.info("用户统计{},{}",begin,end);

UserReportVO userReportVO = reportService.getUserStatistics(begin,end);
return Result.success(userReportVO);
    }
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end){

        return Result.success(reportService.getOrderStatistics(begin,end));
    }
    @GetMapping("/top10")
    @ApiOperation("销量排名统计")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(reportService.getSalesTop10(begin,end));
    }

}
