package com.example.projectschedulehaircutserver.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDTO {
    private Integer id;
    private String name;
    private String image;
    private BigDecimal price;
    private Integer haircutTime;

    public ServiceDTO(Integer id, String name, String image, BigDecimal price, Integer haircutTime) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.haircutTime = haircutTime;
    }
}
