package com.example.projectschedulehaircutserver.service.order;

import com.example.projectschedulehaircutserver.dto.OrderDTO;
import com.example.projectschedulehaircutserver.entity.*;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.exeption.OrderException;
import com.example.projectschedulehaircutserver.repository.*;
import com.example.projectschedulehaircutserver.request.ActionOrderByCustomerRequest;
import com.example.projectschedulehaircutserver.request.AllOrderEmployeeAndDateRequest;
import com.example.projectschedulehaircutserver.request.OrderScheduleHaircutRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final EmployeeRepo employeeRepo;
    private final ComboRepo comboRepo;
    private final ServiceRepo serviceRepo;
    private final CouponsRepo couponsRepo;

    @Override
    @Transactional
    public String BookingScheduleHaircut(OrderScheduleHaircutRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                Set<Employee> employees = new HashSet<>();
                request.getEmployeeId().forEach(eid -> {
                    employees.add(employeeRepo.findById(eid).orElseThrow());
                });

                for (Employee employee : employees) {
                    List<Orders> orders = orderRepo.findAllOrderBooking(employee.getId());
                    if (!orders.isEmpty()) {
                        for (Orders o : orders) {
                            if (o.getOrderDate().equals(request.getOrderDate()) &&
                                    isTimeBetween(request.getOrderStartTime(), o.getOrderStartTime(), o.getOrderEndTime())) {
                                throw new OrderException("Nhân viên bạn muốn đặt lịch hiện đang có lịch trùng với lịch hẹn của bạn, " +
                                        "vui lòng chọn ngày khác hoặc thay đổi giờ");
                            }
                        }
                    }
                }

                Coupons coupons = couponsRepo.findCouponByCustomerId(customer.getId()).orElseThrow();

                BigDecimal discount = request.getTotalPrice().multiply((coupons != null) ? BigDecimal.valueOf(coupons.getDiscount()) : BigDecimal.ONE);
                BigDecimal totalOrder = request.getTotalPrice().subtract(discount);

                Orders orders = Orders.builder()
                        .orderDate(request.getOrderDate())
                        .orderStartTime(request.getOrderStartTime())
                        .orderEndTime(request.getOrderStartTime().plusMinutes(request.getHaircutTime()))
                        .status(1)
                        .haircutTime(request.getHaircutTime())
                        .totalPrice(totalOrder)
                        .customer(customer)
                        .employees(employees)
                        .build();

                Orders saveOrder = orderRepo.save(orders);

                Combo combo = comboRepo.findComboById(request.getComboId());

                // add combo
                if (combo != null && !employees.isEmpty()){
                    OrderItem orderItem = OrderItem.builder()
                            .orders(saveOrder)
                            .combo(combo)
                            .service(null)
                            .price(combo.getPrice())
                            .build();

                    orderItemRepo.save(orderItem);
                }

                Set<com.example.projectschedulehaircutserver.entity.Service> services = new HashSet<>();

                request.getServiceId().forEach(sid -> {
                    services.add(serviceRepo.findById(sid).orElseThrow());
                });

                if (combo != null){
                    for (com.example.projectschedulehaircutserver.entity.Service service : services) {
                        if (combo.getServices().stream().anyMatch(check -> Objects.equals(check.getId(), service.getId()))) {
                            throw new OrderException("Dịch vụ đã có trong combo, " +
                                    "vui lòng thêm một dịch vụ khác ngoài combo đã chọn");
                        }
                    }
                }

                // add service
                if (!services.isEmpty() && !employees.isEmpty()){
                    services.forEach(service -> {
                        OrderItem orderItem = OrderItem.builder()
                                .orders(saveOrder)
                                .combo(null)
                                .service(service)
                                .price(service.getPrice())
                                .build();

                        orderItemRepo.save(orderItem);
                    });
                }
                return "Đặt Lịch Cắt Tóc Thành Công !!!";
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_0() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 0);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_1() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 1);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_2() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 2);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> findAllOrderByEmployeeAndDate(AllOrderEmployeeAndDateRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Employee employee = (Employee) authentication.getPrincipal();
                return orderRepo.findAllOrderByEmployeeAndDate(employee.getId(), 1, request.getOrderDate())
                        .stream()
                        .map(OrderDTO::new)
                        .collect(Collectors.toSet());
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Transactional
    @Override
    public String ConfirmDoneOrCancelOrderedByCustomer(ActionOrderByCustomerRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();

                orderRepo.updateOrdersByStatus(request.getOrderId(), customer.getId(), request.getStatus());

                return "Thành Công";
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    public Set<OrderDTO> showOrderByCustomerStatus(Integer customerId, Integer status){
        List<Object[]> objects = orderRepo.findOrdersByCustomerId(customerId, status);

        return objects.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toSet());
    }


    public boolean isTimeBetween(LocalTime target, LocalTime startTime, LocalTime endTime) {
        if (endTime.isAfter(startTime)) {
            return target.equals(startTime) || target.isAfter(startTime) && target.isBefore(endTime);
        } else {
            return target.isAfter(startTime) || target.isBefore(endTime);
        }
    }


}
