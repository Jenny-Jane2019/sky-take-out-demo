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
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 根据时间区间统计营业额
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate beginTime, LocalDate endTime) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(beginTime);
        while(!beginTime.equals(endTime)){
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }
        // 获取营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            // 获取当天的最早时间点
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", begin);
            map.put("end", end);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        // 封装最终数据
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(dateList,","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList,","));

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate beginTime, LocalDate endTime) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(beginTime);
        while(!beginTime.equals(endTime)){
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }
        List<Integer> newUserList = new ArrayList<>(); //新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //总用户数
        for(LocalDate date : dateList){
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUser = getUserCount(begin, end);
            Integer totalUser = getUserCount(null, end);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
        return userReportVO;
    }
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime){
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate beginTime, LocalDate endTime) {
        // 获取日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(beginTime);
        while(!beginTime.equals(endTime)){
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }
        List<Integer> orderCountList = new ArrayList<>(); //订单数列表
        List<Integer> validOrderCountList = new ArrayList<>(); //有效订单列表（已完成）
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        for(LocalDate date : dateList){
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin",begin);
            map.put("end", end);
            orderCountList.add(orderMapper.countByMap(map));
            totalOrderCount += orderCountList.get(orderCountList.size() - 1);
            map.put("status", Orders.COMPLETED);
            validOrderCountList.add(orderMapper.countByMap(map));
            validOrderCount += validOrderCountList.get(validOrderCountList.size() - 1);
        }
        //封装VO
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue()/totalOrderCount;
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .build();
        return orderReportVO;
    }

    /**
     * 查询指定时间区间内的销量排名top10
     * @param begin
     * @param end
     * @return
     * */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end){
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
