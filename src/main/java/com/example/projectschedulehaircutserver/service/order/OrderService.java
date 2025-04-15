package com.example.projectschedulehaircutserver.service.order;

import com.example.projectschedulehaircutserver.dto.OrderDTO;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.request.ActionOrderByCustomerRequest;
import com.example.projectschedulehaircutserver.request.AllOrderEmployeeAndDateRequest;
import com.example.projectschedulehaircutserver.request.OrderScheduleHaircutRequest;

import java.util.Date;
import java.util.Set;

public interface OrderService {
    String BookingScheduleHaircut(OrderScheduleHaircutRequest request) throws LoginException;

    Set<OrderDTO> showOrderByCustomerStatus_0() throws LoginException;

    Set<OrderDTO> showOrderByCustomerStatus_1() throws LoginException;

    Set<OrderDTO> showOrderByCustomerStatus_2() throws LoginException;

    Set<OrderDTO> findAllOrderByEmployeeAndDate(AllOrderEmployeeAndDateRequest request) throws LoginException;

    String ConfirmDoneOrCancelOrderedByCustomer(ActionOrderByCustomerRequest request) throws LoginException;
}
