package com.example.projectschedulehaircutserver.service.employee;

import com.example.projectschedulehaircutserver.dto.EmployeeDTO;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.request.TotalPriceByEmployeeAndDayRequest;
import com.example.projectschedulehaircutserver.response.TotalPriceByEmployeeAndDayResponse;

import java.util.Set;

public interface EmployeeService {
    void createEmployee(EmployeeDTO employeeDTO);

    Set<EmployeeDTO> showAllEmployee();

    TotalPriceByEmployeeAndDayResponse TotalPriceByEmployeeAndDay(TotalPriceByEmployeeAndDayRequest request) throws LoginException;

}
