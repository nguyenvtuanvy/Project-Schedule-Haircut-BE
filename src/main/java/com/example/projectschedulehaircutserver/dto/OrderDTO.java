package com.example.projectschedulehaircutserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private Date orderDate;
    private Time orderStartTime;
    private Time orderEndTime;
    private List<String> employeeName;
    private String comboName;
    private List<String> serviceName;
    private BigDecimal totalPrice;
    private Integer status;

    public OrderDTO(Object[] data) {
        this.id = (Integer) data[0];
        this.orderDate = (Date) data[1];
        this.orderStartTime = (Time) data[2];
        this.orderEndTime = (Time) data[3];
        this.employeeName = Arrays.asList(((String) data[4]).split(","));
        if (data[5] != null){
            this.comboName = (String) data[5];
        } else {
            this.serviceName = Arrays.asList(((String) data[6]).split(","));
        }
        this.totalPrice = (BigDecimal) data[7];
        this.status = (Integer) data[8];
    }

}
