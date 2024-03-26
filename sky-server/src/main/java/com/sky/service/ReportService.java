package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 根据时间区间统计营业额
     * @param beginTime
     * @param endTime
     * @return
     */
    TurnoverReportVO getTurnover(LocalDate beginTime, LocalDate endTime);

    /**
     * 用户数据统计
     * @param beginTime
     * @param endTime
     * @return
     */
    UserReportVO getUserStatistics(LocalDate beginTime, LocalDate endTime);

    /**
     * 根据时间区间统计订单数量
     * @param beginTime
     * @param endTime
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate beginTime, LocalDate endTime);

    /**
     * 查询指定时间区间内的销量排名top10
     * @param beginTime
     * @param endTime
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate beginTime, LocalDate endTime);
}
