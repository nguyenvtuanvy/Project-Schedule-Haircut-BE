package com.example.projectschedulehaircutserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Integer id;
    private String userName;
    private String password;
    private String email;
    private String fullName;
    private Integer age;
    private String address;
    private String phone;
//    private Integer accountId;
    private String avatar;
//    private Boolean isDeleted;

    public EmployeeDTO(String userName, String password, String fullName, Integer age, String address, String phone, String avatar, String email) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.email = email;
    }

    public EmployeeDTO(Integer id, String userName, String fullName, Integer age, String address, String phone, String avatar) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
    }
}
